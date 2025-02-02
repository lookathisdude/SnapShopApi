package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.DTO.Product.*;
import com.W3yneRagsac.SnapShop.exceptions.ProductAlreadyFoundException;
import com.W3yneRagsac.SnapShop.exceptions.ProductNotFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.model.Entity.ProductEntity;

import java.util.Optional;

public interface IProductService {
    ProductEntity createProduct(CreateProductDTO createProductDTO, String timeZone) throws UserFoundException, ProductAlreadyFoundException;
    ProductEntity updateProduct(UpdatedProductDTO updatedProductDTO, String timeZone) throws ProductNotFoundException;
    ProductEntity deleteProduct(DeleteProductDTO deleteProductDTO, String timeZone) throws ProductNotFoundException;
    // Method to get a product by its ID
    Optional<ProductEntity> getProductByID(Long productId);
    // Method to get a product by its name
    Optional<ProductEntity> getProductByName(String productName);
}
