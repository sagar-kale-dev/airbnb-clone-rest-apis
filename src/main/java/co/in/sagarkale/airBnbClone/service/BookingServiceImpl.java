package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.BookingDto;
import co.in.sagarkale.airBnbClone.dto.BookingReqDto;
import co.in.sagarkale.airBnbClone.dto.GuestDto;
import co.in.sagarkale.airBnbClone.dto.HotelReportDto;
import co.in.sagarkale.airBnbClone.entity.*;
import co.in.sagarkale.airBnbClone.entity.enums.BookingStatus;
import co.in.sagarkale.airBnbClone.exception.ResourceNotFoundException;
import co.in.sagarkale.airBnbClone.exception.UnAuthorizedException;
import co.in.sagarkale.airBnbClone.repository.*;
import co.in.sagarkale.airBnbClone.strategy.pricing.PricingService;
import co.in.sagarkale.airBnbClone.util.AppUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingReqDto bookingReqDto) {
//        Check if hotel and room mentioned in booking request exists
//        Find inventory for given room withing mentioned check in/out date with room count
//        Check if inventory count is matching check in/out days count
//        Mark inventories reserved
//        Add entry in booking table and return reserved booking details
        Hotel hotel = hotelRepository.findById(bookingReqDto.getHotelId()).orElseThrow(
                ()-> new ResourceNotFoundException(
                        "Hotel not found with id " + bookingReqDto.getHotelId()
                )
        );
        Room room = roomRepository.findById(bookingReqDto.getRoomId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Room not found with id " + bookingReqDto.getRoomId()
                )
        );

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                bookingReqDto.getRoomId(),
                bookingReqDto.getCheckInDate(),
                bookingReqDto.getCheckOutDate(),
                bookingReqDto.getRoomsCount()
        );
        long daysCount = ChronoUnit.DAYS.between(
                bookingReqDto.getCheckInDate(),
                bookingReqDto.getCheckOutDate()
        ) + 1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        inventoryRepository.initBooking(
                bookingReqDto.getRoomId(),
                bookingReqDto.getCheckInDate(),
                bookingReqDto.getCheckOutDate(),
                bookingReqDto.getRoomsCount()
        );

        BigDecimal priceForOneRoom = pricingService.calculateTotalPricing(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingReqDto.getRoomsCount()));

        Booking booking = Booking
                .builder()
                .hotel(hotel)
                .room(room)
                .user(AppUtils.getCurrentUser())
                .roomsCount(bookingReqDto.getRoomsCount())
                .bookingStatus(BookingStatus.RESERVED)
                .checkInDate(bookingReqDto.getCheckInDate())
                .checkOutDate(bookingReqDto.getCheckOutDate())
                .amount(totalPrice)
                .build();
        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
//        Check booking id is valid
//        Check if booking belongs to user requesting to add guest
//        Check booking is created/reserved 10 mins before only otherwise booking will be treated as expired
//        Check booking to which adding guest is in reserved state otherwise mark invalid request
//        Hardcode the user to each guest
//        Add guests to booking
//        Set booking status to guest added

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id "+ bookingId)
        );

        User user = AppUtils.getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belongs to user with id: "+user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has been already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not in reserved state, failed to add guest");
        }

        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id "+ bookingId)
        );

        User user = AppUtils.getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belongs to user with id: "+user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has been already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(
                booking,
                frontEndUrl+"/payments/"+bookingId+"/status",
                frontEndUrl+"/payments/"+bookingId+"/status"
        );

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if(event.getType().equals("checkout.session.completed")){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if(session == null) return;

            String sessionId = session.getId();

            Booking booking = bookingRepository.findBookingByPaymentSessionId(sessionId)
                    .orElseThrow(()-> new ResourceNotFoundException("No booking found with payment session id : "+ sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getRoomsCount(), booking.getCheckInDate(),
                    booking.getCheckOutDate());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getRoomsCount(), booking.getCheckInDate(),
                    booking.getCheckOutDate());
        }else{
            log.info("Unhandled event type: "+event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );

        User user = AppUtils.getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belongs to user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getRoomsCount(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getRoomsCount(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public BookingStatus bookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );

        User user = AppUtils.getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belongs to user with id: "+user.getId());
        }

        return booking.getBookingStatus();
    }

    @Override
    public List<BookingDto> getAllBookingByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id: "+hotelId));

        User user = AppUtils.getCurrentUser();

        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);
        }

        List<Booking> bookingList = bookingRepository.findByHotel(hotel);
        return bookingList.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: "+ hotelId));
        User user = AppUtils.getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("You are not the owner of hotel with id: "+ hotelId);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookingList = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        long totalConfirmedBookings = bookingList.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookingList.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenueOfConfirmedBookings = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmedBookings,totalRevenueOfConfirmedBookings,avgRevenueOfConfirmedBookings);
    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user = AppUtils.getCurrentUser();
        List<Booking> bookingList = bookingRepository.findByUser(user);
        return bookingList.stream().map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toList());
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
