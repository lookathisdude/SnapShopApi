package com.W3yneRagsac.SnapShop.DTO.Product;

import com.W3yneRagsac.SnapShop.model.ProductEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductDTO {
    // NotNull are errors
    // size of characters

    @NotNull(message = "Product name cannot be null")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    private String productName;

    @NotNull(message = "Product description cannot be null")
    @Size(min = 1, max = 1000, message = "Product description must be between 1 and 1000 characters")
    private String productDescription;

    @NotNull(message = "Price cannot be null")
    private Float price;

    @NotNull(message = "Currency cannot be null")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters (e.g., USD)")
    private String currency;

    @NotNull(message = "Category cannot be null")
    @Size(min = 1, max = 255, message = "Category must be between 1 and 255 characters")
    private String category;          // Product category

    @NotNull(message = "Stock cannot be null")
    private Integer stock;            // Number of items in stock

    @NotNull(message = "Images cannot be null")
    private List<String> image;       // List of image URLs or file paths

    @NotNull(message = "Tags cannot be null")
    private List<String> tags;
}
