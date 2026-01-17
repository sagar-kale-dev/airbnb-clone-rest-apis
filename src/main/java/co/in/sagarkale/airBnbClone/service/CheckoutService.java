package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
