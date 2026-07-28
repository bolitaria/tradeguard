package com.talan.tradeguard.service;

import com.talan.tradeguard.dto.TradeRequest;
import com.talan.tradeguard.dto.TradeResponse;
import com.talan.tradeguard.exception.TradeValidationException;
import com.talan.tradeguard.model.TradeOrder;
import com.talan.tradeguard.repository.TradeOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TradeValidationService {
    private static final Logger log = LoggerFactory.getLogger(TradeValidationService.class);
    private static final String REASON_RISK_LIMIT = "Exceeds risk limit";
    private static final String REASON_RESTRICTED_TRADER = "Trader in restricted list";

    @Value("${app.risk.limit}")
    private double riskLimit;

    private final TradeOrderRepository repository;

    public TradeValidationService(TradeOrderRepository repository) {
        this.repository = repository;
    }

    public TradeResponse validateAndSave(TradeRequest request) {
        log.info("Validating trade for {}: {} shares of {} at {}",
                request.getTraderName(), request.getQuantity(), request.getSymbol(), request.getPrice());

        TradeOrder order = mapToEntity(request);

        if (order.getQuantity() * order.getPrice() > riskLimit) {
            order.setStatus("REJECTED");
            order.setRejectReason(REASON_RISK_LIMIT);
            log.warn("Trade rejected for {}: {}", request.getTraderName(), REASON_RISK_LIMIT);
        } else if ("SuspiciousTrader".equalsIgnoreCase(order.getTraderName())) {
            order.setStatus("REJECTED");
            order.setRejectReason(REASON_RESTRICTED_TRADER);
            log.warn("Trade rejected for {}: {}", request.getTraderName(), REASON_RESTRICTED_TRADER);
        } else {
            order.setStatus("APPROVED");
        }

        TradeOrder saved = repository.save(order);
        log.info("Trade saved with id {} and status {}", saved.getId(), saved.getStatus());
        return mapToResponse(saved);
    }

    private TradeOrder mapToEntity(TradeRequest request) {
        TradeOrder order = new TradeOrder();
        order.setSymbol(request.getSymbol());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order.setTraderName(request.getTraderName());
        return order;
    }

    private TradeResponse mapToResponse(TradeOrder order) {
        return new TradeResponse(
                order.getId(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getTraderName(),
                order.getStatus(),
                order.getRejectReason()
        );
    }
}
