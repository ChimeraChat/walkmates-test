package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Lab 1, Part B — specification-based tests for {@link Seeker}.
 *
 * <p>Design your tests on paper first (equivalence partitions, boundary values, decision table)
 * from {@code docs/REQUIREMENTS.md} FR-1.1 / FR-1.3 / FR-1.2, then implement them here. One
 * worked example is provided; the {@code TODO}s are yours.</p>
 */
class SeekerSpecBasedTest {

    // ---- Worked example: boundary value at the maximum single top-up (FR-1.3) ----
    @Test
    @DisplayName("Top-up exactly at the 5000 SEK single-transaction maximum is accepted")
    void topUpAtSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("sam@example.com", "Sam", "0707654321");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP); // 5000.00, the boundary value

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_SINGLE_TOP_UP);
    }

    // Activity 2.1: equivalence partitions from FR-1.1 and FR-1.3.

    @Test
    void validEmailIsAccepted() {
        Seeker seeker = new Seeker("louise@miun.se", "Louise Larsen", "0763000843");
        assertThat(seeker.getEmail()).isEqualTo("louise@miun.se");
    }

    @Test
    void emailWithoutAtIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("louise.miun.se", "Louise Larsen", "0763000843"));
    }

    @Test
    void emailOver254CharactersIsRejected() {
        String email = "P".repeat(260) + "@miun.se";
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(email, "Louise Larsen", "0763000843"));
    }

    @Test
    void validDisplayNameIsAccepted() {
        Seeker seeker = new Seeker("louise@miun.se", "Louise Larsen", "0763000843");
        assertThat(seeker.getDisplayName()).isEqualTo("Louise Larsen");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "L8tta",
            "Lottafannieshellaandgooliatformelodyandjohanndesa",
            "L"
    })
    void invalidDisplayNamesAreRejected(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("louise@miun.se", name, "0763000843"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0763000843", "+4673000843"})
    void validPhoneFormatsAreAccepted(String phone) {
        Seeker seeker = new Seeker("louise@miun.se", "Louise Larsen", phone);
        assertThat(seeker.getPhoneNumber()).isEqualTo(phone);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0853000843", "076300084399"})
    void invalidPhoneFormatsAreRejected(String phone) {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("louise@miun.se", "Louise Larsen", phone));
    }

    @Test
    void topUpBelowMinimumIsRejected() {
        assertRejectedTopUpLeavesBalance(newSeeker(), 8.00, 0.00);
    }

    @Test
    void topUpOverMaximumBalanceIsRejected() {
        Seeker seeker = seekerWithBalance(19600.00);
        assertRejectedTopUpLeavesBalance(seeker, 500.00, 19600.00);
    }

    // Activity 2.2: wallet boundaries from FR-1.3.

    @Test
    void topUpJustBelowMinimumIsRejected() {
        assertRejectedTopUpLeavesBalance(newSeeker(), 9.99, 0.00);
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.00, 10.01})
    void topUpsAtAndJustAboveMinimumAreAccepted(double amount) {
        Seeker seeker = newSeeker();
        seeker.addFunds(amount);
        assertThat(seeker.getBalance()).isEqualTo(amount);
    }

    @Test
    void topUpJustBelowSingleMaximumIsAccepted() {
        Seeker seeker = newSeeker();
        seeker.addFunds(4999.99);
        assertThat(seeker.getBalance()).isEqualTo(4999.99);
    }

    @Test
    void topUpJustAboveSingleMaximumIsRejected() {
        assertRejectedTopUpLeavesBalance(newSeeker(), 5000.01, 0.00);
    }

    @ParameterizedTest
    @CsvSource({"19899.99, 19999.99", "19900.00, 20000.00"})
    void balancesJustBelowAndAtMaximumAreAccepted(double startingBalance, double expectedBalance) {
        Seeker seeker = seekerWithBalance(startingBalance);
        seeker.addFunds(100.00);
        assertThat(seeker.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    void balanceJustAboveMaximumIsRejected() {
        Seeker seeker = seekerWithBalance(19900.01);
        assertRejectedTopUpLeavesBalance(seeker, 100.00, 19900.01);
    }

    // Activity 2.3: trust-tier decision table from FR-1.2.

    @ParameterizedTest
    @CsvSource({
            "NEW, 1, 0.15",
            "VERIFIED, 3, 0.12",
            "TRUSTED, 5, 0.08",
            "PRO_SITTER, 10, 0.05"
    })
    void trustTierDeterminesBookingLimitAndFee(TrustTier tier, int maxBookings, double fee) {
        Seeker seeker = newSeeker();
        seeker.setTrustTier(tier);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(maxBookings);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(fee);
    }

    private static Seeker newSeeker() {
        return new Seeker("louise@miun.se", "Louise Larsen", "0763000843");
    }

    private static Seeker seekerWithBalance(double balance) {
        Seeker seeker = newSeeker();
        for (int i = 0; i < 3; i++) {
            seeker.addFunds(5000.00);
        }
        seeker.addFunds(balance - 15000.00);
        assertThat(seeker.getBalance()).isEqualTo(balance);
        return seeker;
    }

    private static void assertRejectedTopUpLeavesBalance(Seeker seeker, double amount, double balance) {
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(amount));
        assertThat(seeker.getBalance()).isEqualTo(balance);
    }
}
