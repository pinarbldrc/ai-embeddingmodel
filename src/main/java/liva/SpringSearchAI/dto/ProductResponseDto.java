package liva.SpringSearchAI.dto;

import liva.SpringSearchAI.model.ProductProperty;

import java.util.List;

public class ProductResponseDto {
    String id;
    String name;
    List<ProductProperty> productProperties;

    String category;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductProperty> getProductProperties() {
        return productProperties;
    }

    public void setProductProperties(List<ProductProperty> productProperties) {
        this.productProperties = productProperties;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
