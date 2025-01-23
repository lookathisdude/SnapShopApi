package com.W3yneRagsac.SnapShop.DTO.Product;

import lombok.Data;

import java.util.List;

@Data
public class UpdatedProductDTO{
    private Long productId;
    private String productName;
    private String productDescription;
    private Float price;
    private String currency;
    private String category;
    private Integer stock;
    private List<String> image;
    private List<String> tags;
}
