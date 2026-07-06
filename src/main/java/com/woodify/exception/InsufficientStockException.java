package com.woodify.exception;

public class InsufficientStockException extends WoodifyException {
    private final String productNama;
    private final int availableStock;
    private final int requestedQty;

    public InsufficientStockException(String productNama, int availableStock, int requestedQty) {
        super(String.format("Stok tidak mencukupi untuk %s. Diminta: %d, Tersedia: %d", 
                productNama, requestedQty, availableStock));
        this.productNama = productNama;
        this.availableStock = availableStock;
        this.requestedQty = requestedQty;
    }

    public String getProductNama() {
        return productNama;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQty() {
        return requestedQty;
    }
}
