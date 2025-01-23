package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.DTO.Product.*;
import com.W3yneRagsac.SnapShop.exceptions.ProductAlreadyFoundException;
import com.W3yneRagsac.SnapShop.exceptions.ProductNotFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.model.ProductEntity;

import java.util.Optional;

public interface IProductService {
    ProductEntity createProduct(CreateProductDTO createProductDTO, String timeZone) throws UserFoundException, ProductAlreadyFoundException;
    ProductEntity updateProduct(UpdatedProductDTO updatedProductDTO, String timeZone) throws ProductNotFoundException;
    ProductEntity deleteProduct(DeleteProductDTO deleteProductDTO, String timeZone) throws ProductNotFoundException;
    Optional <ProductEntity> getProductByID(GetProductByIdDTO getProductByIdDTO);
    Optional <ProductEntity> getProductByName(GetProductByNameDTO getProductByNameDTO);

}
