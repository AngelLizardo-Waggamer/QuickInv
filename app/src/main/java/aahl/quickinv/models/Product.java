package aahl.quickinv.models;

import java.io.Serializable;

/**
 * Modelo que representa un Producto en la base de datos.
 */
public class Product implements Serializable {

    private long id;
    private String name;
    private int quantity;
    private double unitPrice;
    private long createdAt;
    private long updatedAt;
    private Long lastCheckedAt; // Puede ser null
    private long inventoryId;

    // Constructor vacío
    public Product() {
    }

    // Constructor completo
    public Product(long id, String name, int quantity, double unitPrice,
                   long createdAt, long updatedAt, Long lastCheckedAt, long inventoryId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastCheckedAt = lastCheckedAt;
        this.inventoryId = inventoryId;
    }

    // Constructor sin fechas (para crear nuevos productos)
    public Product(String name, int quantity, double unitPrice, long inventoryId) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.inventoryId = inventoryId;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Long lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(long inventoryId) {
        this.inventoryId = inventoryId;
    }

    /**
     * Calcula el precio total (cantidad * precio unitario).
     */
    public double getTotalPrice() {
        return quantity * unitPrice;
    }

    /**
     * Verifica si el producto ha sido checkeado.
     */
    public boolean isChecked() {
        return lastCheckedAt != null;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastCheckedAt=" + lastCheckedAt +
                ", inventoryId=" + inventoryId +
                '}';
    }
}

