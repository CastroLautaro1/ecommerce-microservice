package com.ecommerce.catalog_service.product.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ImageEmbeddable {

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "is_main", nullable = false)
    private boolean isMain;

    protected ImageEmbeddable() {}

    public ImageEmbeddable(String url, boolean isMain) {
        this.url = url;
        this.isMain = isMain;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isMain() { return isMain; }
    public void setMain(boolean main) { isMain = main; }
}
