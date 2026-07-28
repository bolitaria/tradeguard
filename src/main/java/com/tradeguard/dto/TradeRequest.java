package com.tradeguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TradeRequest {
    @NotBlank(message = "Symbol is mandatory")
    private String symbol;

    @Positive(message = "Quantity must be positive")
    private double quantity;

    @Positive(message = "Price must be positive")
    private double price;

    @NotBlank(message = "Trader name is mandatory")
    private String traderName;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getTraderName() { return traderName; }
    public void setTraderName(String traderName) { this.traderName = traderName; }
}
