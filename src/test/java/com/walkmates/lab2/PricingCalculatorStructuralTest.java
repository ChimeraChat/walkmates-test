package com.walkmates.lab2;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import com.walkmates.service.PricingCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 2, Part A — structural testing for {@link PricingCalculator} (FR-4.3).
 *
 * <p>Run coverage with {@code mvn clean test jacoco:report} and open
 * {@code target/site/jacoco/index.html}. Find the uncovered branches and add tests to reach
 * them — then look hard at the <em>overnight surcharge boundary</em>: there is a path that your
 * happy-path test "covers" but does not actually check (coverage ≠ correctness).</p>
 */
class PricingCalculatorStructuralTest {

    private final PricingCalculator pricing = new PricingCalculator();

    private Seeker seeker(TrustTier tier) {
        Seeker s = new Seeker("p@example.com", "Pat", "0701112233");
        s.setTrustTier(tier);
        return s;
    }

    private Listing listing(ListingType type) {
        return new Listing("provider-1", "A listing", "desc", type);
    }

    // ---- Worked example: a short standard walk, no overnight surcharge ----
    @Test
    @DisplayName("60 min DOG_WALK for a VERIFIED seeker = 80 base + 12% fee = 89.60")
    void shortWalkPrice() {
        Booking booking = new Booking("seeker-1", "listing-1", 60);

        double price = pricing.priceFor(booking, listing(ListingType.DOG_WALK), seeker(TrustTier.VERIFIED));

        assertThat(price).isEqualTo(89.60);
    }

    // TODO (branch): a free SHELTER_VOLUNTEER listing always costs 0.00.
    @Test
    @DisplayName("SHELTER_VOLUNTEER always cost 0.00 SEK")
    void volunteerAlwaysCostZero() {
        Booking booking = new Booking("seeker-1", "listing-2", 30);

        double price = pricing.priceFor(booking, listing(ListingType.SHELTER_VOLUNTEER), seeker(TrustTier.VERIFIED));

        assertThat(price).isEqualTo(0.00);
    }

    // TODO (branch): a clearly-overnight booking (e.g. 600 min) includes the 20% surcharge.
    @Test
    @DisplayName("Overnight booking include 20% surcharge = 800 base + 20% charge + 12% fee = 1075.20 SEK")
    void overnightBookingIncludeSurcharge() {
        Booking booking = new Booking("seeker-1", "listing-3", 600);

        double price = pricing.priceFor(booking, listing(ListingType.DOG_WALK), seeker(TrustTier.VERIFIED));

        assertThat(price).isEqualTo(1075.20);
    }

    // TODO (BOUNDARY — this is the interesting one): a booking of exactly 480 minutes must NOT
    //      be surcharged (FR-4.3 says strictly > 480). Write this test and see what happens.
    @Test
    @DisplayName("Booking exactly 480 minutes should not trigger surcharge")
    void surchargeLimitNoSurcharge() {
        Booking booking = new Booking("seeker-1", "listing-4", 480);

        double price = pricing.priceFor(booking, listing(ListingType.DOG_WALK), seeker(TrustTier.VERIFIED));

        assertThat(price).isEqualTo(716.80);
    }
}
