package com.walkmates.lab2;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.Seeker;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.BookingService;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PricingCalculator;
import com.walkmates.model.BookingStatus;
import com.walkmates.model.ListingType;
import com.walkmates.model.Provider;
import com.walkmates.model.ListingStatus;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class BookingServiceTest {

    @Test
    void rejectsBookingWhenSeekerIsAtLimit() {
        SeekerRepository seekers = mock(SeekerRepository.class);
        ListingRepository listings = mock(ListingRepository.class);
        ProviderRepository providers = mock(ProviderRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        PricingCalculator pricing = mock(PricingCalculator.class);
        NotificationService notifications = mock(NotificationService.class);

        BookingService service = new BookingService(
                seekers, listings, providers, bookings, pricing, notifications);

        Seeker seeker = mock(Seeker.class);
        Listing listing = mock(Listing.class);
        Booking existingBooking = mock(Booking.class);

        when(seekers.findById("seeker-1")).thenReturn(Optional.of(seeker));
        when(listings.findById("listing-1")).thenReturn(Optional.of(listing));
        when(listing.isAvailable()).thenReturn(true);

        when(seeker.getMaxConcurrentBookings()).thenReturn(1);
        when(existingBooking.isActive()).thenReturn(true);
        when(bookings.findBySeekerId("seeker-1"))
                .thenReturn(List.of(existingBooking));

        assertThatThrownBy(() ->
                service.createBooking("seeker-1", "listing-1", 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessageContaining("Seeker booking limit reached");
    }

    @Test
    void successfulBookingSendsConfirmation() {
        SeekerRepository seekers = mock(SeekerRepository.class);
        ListingRepository listings = mock(ListingRepository.class);
        ProviderRepository providers = mock(ProviderRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        PricingCalculator pricing = mock(PricingCalculator.class);
        NotificationService notifications = mock(NotificationService.class);

        BookingService service = new BookingService(
                seekers, listings, providers, bookings, pricing, notifications);

        Seeker seeker = new Seeker(
                "pat@example.com", "Pat", "0701112233");
        seeker.addFunds(200.00);

        Provider provider = new Provider("Dog owner", 63.0, 14.0);
        Listing listing = new Listing(
                provider.getId(), "Dog walk", "Walk the dog", ListingType.DOG_WALK);

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId()))
                .thenReturn(Optional.of(listing));
        when(providers.findById(provider.getId()))
                .thenReturn(Optional.of(provider));

        when(bookings.findBySeekerId(seeker.getId()))
                .thenReturn(List.of());
        when(listings.findByProviderId(provider.getId()))
                .thenReturn(List.of(listing));
        when(bookings.findByListingId(listing.getId()))
                .thenReturn(List.of());

        when(pricing.priceFor(any(Booking.class), eq(listing), eq(seeker)))
                .thenReturn(100.00);

        Booking booking = service.createBooking(
                seeker.getId(), listing.getId(), 60);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getPrice()).isEqualTo(100.00);
        assertThat(seeker.getBalance()).isEqualTo(100.00);
        assertThat(listing.getStatus()).isEqualTo(ListingStatus.BOOKED);

        verify(notifications).sendBookingConfirmed(seeker, booking);
    }

    @Test
    void rejectsBookingWhenProviderIsAtCapacity() {
        SeekerRepository seekers = mock(SeekerRepository.class);
        ListingRepository listings = mock(ListingRepository.class);
        ProviderRepository providers = mock(ProviderRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        PricingCalculator pricing = mock(PricingCalculator.class);
        NotificationService notifications = mock(NotificationService.class);

        BookingService service = new BookingService(
                seekers, listings, providers, bookings, pricing, notifications);

        Seeker seeker = new Seeker(
                "pat@example.com", "Pat", "0701112233");
        Provider provider = new Provider("Dog owner", 63.0, 14.0, 1);

        Listing requestedListing = new Listing(
                provider.getId(), "Dog walk", "Walk the dog", ListingType.DOG_WALK);
        Listing occupiedListing = new Listing(
                provider.getId(), "Another walk", "Already booked",
                ListingType.DOG_WALK);

        Booking existingBooking = new Booking(
                "another-seeker", occupiedListing.getId(), 60);
        existingBooking.transitionTo(BookingStatus.CONFIRMED);
        occupiedListing.transitionTo(ListingStatus.BOOKED);

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));
        when(listings.findById(requestedListing.getId()))
                .thenReturn(Optional.of(requestedListing));
        when(bookings.findBySeekerId(seeker.getId()))
                .thenReturn(List.of());
        when(providers.findById(provider.getId()))
                .thenReturn(Optional.of(provider));
        when(listings.findByProviderId(provider.getId()))
                .thenReturn(List.of(requestedListing, occupiedListing));
        when(bookings.findByListingId(requestedListing.getId()))
                .thenReturn(List.of());
        when(bookings.findByListingId(occupiedListing.getId()))
                .thenReturn(List.of(existingBooking));

        assertThatThrownBy(() ->
                service.createBooking(
                        seeker.getId(), requestedListing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessage("Provider is at capacity");
    }

}

