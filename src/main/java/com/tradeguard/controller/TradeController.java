package com.tradeguard.controller;

import com.tradeguard.dto.TradeRequest;
import com.tradeguard.dto.TradeResponse;
import com.tradeguard.model.TradeOrder;
import com.tradeguard.repository.TradeOrderRepository;
import com.tradeguard.service.TradeValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeValidationService validationService;
    private final TradeOrderRepository repository;

    public TradeController(TradeValidationService validationService, TradeOrderRepository repository) {
        this.validationService = validationService;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<TradeResponse> validateTrade(@Valid @RequestBody TradeRequest request) {
        TradeResponse response = validationService.validateAndSave(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{trader}")
    public ResponseEntity<List<TradeResponse>> getHistory(@PathVariable String trader) {
        List<TradeOrder> orders = repository.findByTraderName(trader);
        List<TradeResponse> responses = orders.stream().map(order ->
                new TradeResponse(
                        order.getId(), order.getSymbol(), order.getQuantity(),
                        order.getPrice(), order.getTraderName(), order.getStatus(),
                        order.getRejectReason()
                )).toList();   // <-- Cambio aquí: .toList() en lugar de .collect(Collectors.toList())
        return ResponseEntity.ok(responses);
    }
}