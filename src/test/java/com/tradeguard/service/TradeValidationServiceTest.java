package com.tradeguard.service;

import com.tradeguard.dto.TradeRequest;
import com.tradeguard.dto.TradeResponse;
import com.tradeguard.model.TradeOrder;
import com.tradeguard.repository.TradeOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeValidationServiceTest {

    @Mock
    private TradeOrderRepository repository;

    @InjectMocks
    private TradeValidationService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "riskLimit", 1_000_000.0);
    }

    @Test
    void shouldApproveValidTrade() {
        TradeRequest req = new TradeRequest();
        req.setSymbol("AAPL");
        req.setQuantity(100);
        req.setPrice(150.0);
        req.setTraderName("John");

        TradeOrder saved = new TradeOrder();
        saved.setId(1L);
        saved.setSymbol("AAPL");
        saved.setQuantity(100);
        saved.setPrice(150.0);
        saved.setTraderName("John");
        saved.setStatus("APPROVED");

        when(repository.save(any(TradeOrder.class))).thenReturn(saved);

        TradeResponse response = service.validateAndSave(req);
        assertThat(response.getStatus()).isEqualTo("APPROVED");
        assertThat(response.getRejectReason()).isNull();

        ArgumentCaptor<TradeOrder> captor = ArgumentCaptor.forClass(TradeOrder.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void shouldRejectExceedingRiskLimit() {
        TradeRequest req = new TradeRequest();
        req.setSymbol("TSLA");
        req.setQuantity(10000);
        req.setPrice(200.0);
        req.setTraderName("BigPlayer");

        TradeOrder saved = new TradeOrder();
        saved.setStatus("REJECTED");
        saved.setRejectReason("Exceeds risk limit");
        when(repository.save(any())).thenReturn(saved);

        TradeResponse response = service.validateAndSave(req);
        assertThat(response.getStatus()).isEqualTo("REJECTED");
        assertThat(response.getRejectReason()).contains("Exceeds risk limit");
    }

    @Test
    void shouldRejectSuspiciousTrader() {
        TradeRequest req = new TradeRequest();
        req.setSymbol("EURUSD");
        req.setQuantity(10);
        req.setPrice(1.05);
        req.setTraderName("SuspiciousTrader");

        TradeOrder saved = new TradeOrder();
        saved.setStatus("REJECTED");
        saved.setRejectReason("Trader in restricted list");
        when(repository.save(any())).thenReturn(saved);

        TradeResponse response = service.validateAndSave(req);
        assertThat(response.getStatus()).isEqualTo("REJECTED");
        assertThat(response.getRejectReason()).contains("restricted list");
    }
}