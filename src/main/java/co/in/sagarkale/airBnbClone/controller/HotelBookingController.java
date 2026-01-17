package co.in.sagarkale.airBnbClone.controller;

import co.in.sagarkale.airBnbClone.dto.*;
import co.in.sagarkale.airBnbClone.entity.enums.BookingStatus;
import co.in.sagarkale.airBnbClone.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "User level hotel booking")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    @Operation(summary = "Book hotel")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingReqDto bookingReqDto){
       return ResponseEntity.ok(bookingService.initializeBooking(bookingReqDto));
    }

    @PostMapping("/{bookingId}/addGuests")
    @Operation(summary = "Add guests to booking")
    public ResponseEntity<BookingDto> addGuest(
            @PathVariable Long bookingId,
            @RequestBody List<GuestDto> guestDtoList
    ){
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

    @PostMapping("/{bookingId}/payments")
    @Operation(summary = "Initiate payment for booking")
    public ResponseEntity<BookingPaymentIntRespDto> addGuest(
            @PathVariable Long bookingId
    ){
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(new BookingPaymentIntRespDto(sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel payment for booking")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long bookingId){
        bookingService.cancelPayment(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    @Operation(summary = "Booking status by booking id")
    public ResponseEntity<BookingStatusRespDto> bookingStatus(@PathVariable Long bookingId){
        BookingStatus bookingStatus = bookingService.bookingStatus(bookingId);
        return ResponseEntity.ok(new BookingStatusRespDto(bookingStatus));
    }

}
