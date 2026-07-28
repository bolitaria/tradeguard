package com.tradeguard.dto;

public class TradeResponse {
    private Long id;
    private String symbol;
    private double quantity;
    private double price;
    private String traderName;
    private String status;
    private String rejectReason;

    public TradeResponse() {}

    public TradeResponse(Long id, String symbol, double quantity, double price,
                         String traderName, String status, String rejectReason) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.traderName = traderName;
        this.status = status;
        this.rejectReason = rejectReason;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public double getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getTraderName() { return traderName; }
    public String getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }

    public void setId(Long id) { this.id = id; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setTraderName(String traderName) { this.traderName = traderName; }
    public void setStatus(String status) { this.status = status; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
