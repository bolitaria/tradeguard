package com.tradeguard.exception;

public class TradeValidationException extends RuntimeException {
    private final String reason;
    public TradeValidationException(String reason) {
        super(reason);
        this.reason = reason;
    }
    public String getReason() { return reason; }
}