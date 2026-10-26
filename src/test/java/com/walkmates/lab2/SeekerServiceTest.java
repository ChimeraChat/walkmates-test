package com.walkmates.lab2;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PaymentService;
import com.walkmates.service.SeekerService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;


public class SeekerServiceTest {

    @Test
    void successfulPaymentCreditsWallet() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers, payments, notifications);

        Seeker seeker = new Seeker(
                "pat@example.com", "Pat", "0701112233");
        seeker.addFunds(50.00);

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));
        when(payments.charge(seeker.getId(), "card-1", 100.00))
                .thenReturn("payment-confirmed");
        when(seekers.save(seeker)).thenReturn(seeker);

        Seeker result = service.topUp(seeker.getId(), "card-1", 100.00);

        assertThat(result).isSameAs(seeker);
        assertThat(seeker.getBalance()).isEqualTo(150.00);
        verify(payments).charge(seeker.getId(), "card-1", 100.00);
        verify(seekers).save(seeker);
    }

    @Test
    void declinedPaymentLeavesWalletUnchanged() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers, payments, notifications);

        Seeker seeker = new Seeker(
                "pat@example.com", "Pat", "0701112233");
        seeker.addFunds(50.00);

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));
        when(payments.charge(seeker.getId(), "card-1", 100.00))
                .thenThrow(new PaymentService.PaymentException("Payment declined"));

        assertThatThrownBy(() ->
                service.topUp(seeker.getId(), "card-1", 100.00))
                .isInstanceOf(PaymentService.PaymentException.class)
                .hasMessage("Payment declined");

        assertThat(seeker.getBalance()).isEqualTo(50.00);
        verify(seekers, never()).save(any(Seeker.class));
    }

    @Test
    void paymentTimeoutLeavesWalletUnchanged() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers, payments, notifications);

        Seeker seeker = new Seeker(
                "pat@example.com", "Pat", "0701112233");
        seeker.addFunds(50.00);

        when(seekers.findById(seeker.getId()))
                .thenReturn(Optional.of(seeker));
        when(payments.charge(seeker.getId(), "card-1", 100.00))
                .thenThrow(new PaymentService.PaymentTimeoutException(
                        "Payment timed out"));

        assertThatThrownBy(() ->
                service.topUp(seeker.getId(), "card-1", 100.00))
                .isInstanceOf(PaymentService.PaymentTimeoutException.class)
                .hasMessage("Payment timed out");

        assertThat(seeker.getBalance()).isEqualTo(50.00);
        verify(seekers, never()).save(any(Seeker.class));
    }
}
