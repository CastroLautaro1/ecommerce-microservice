package com.ecommerce.catalog_service.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class PriceEmbeddable {

    @Column(name = "price_amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "price_currency", length = 3)
    private String currency;

    // Constructor sin argumentos exigido por Hibernate
    protected PriceEmbeddable() {}

    public PriceEmbeddable(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }


    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

}
