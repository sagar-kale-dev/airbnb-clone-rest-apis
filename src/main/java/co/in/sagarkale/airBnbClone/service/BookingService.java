package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.BookingDto;
import co.in.sagarkale.airBnbClone.dto.BookingReqDto;
import co.in.sagarkale.airBnbClone.dto.GuestDto;
import co.in.sagarkale.airBnbClone.dto.HotelReportDto;
import co.in.sagarkale.airBnbClone.entity.enums.BookingStatus;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto initializeBooking(BookingReqDto bookingReqDto);
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
    String initiatePayments(Long bookingId);
    void capturePayment(Event event);
    void cancelPayment(Long bookingId);
    BookingStatus bookingStatus(Long bookingId);
    List<BookingDto> getAllBookingByHotelId(Long hotelId);
    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
    List<BookingDto> getMyBookings();
}
