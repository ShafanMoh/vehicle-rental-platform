package com.driveeasy.rental.dto;

/**
 * Price breakdown for a booking, shown before the customer confirms.
 */
public record RentalQuote(long days, double baseCost, double discount, double total, String pricingNote) {
}
