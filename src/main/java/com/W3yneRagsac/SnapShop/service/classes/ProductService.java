package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.DTO.Product.*;
import com.W3yneRagsac.SnapShop.exceptions.ProductAlreadyFoundException;
import com.W3yneRagsac.SnapShop.exceptions.ProductNotFoundException;
import com.W3yneRagsac.SnapShop.model.ProductEntity;
import com.W3yneRagsac.SnapShop.repository.ProductRepository;
import com.W3yneRagsac.SnapShop.service.interfaces.IProductService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;
import java.util.Optional;

public class ProductService implements IProductService {

    // Product repository is injected into the service to access the product data
    private final ProductRepository productRepository;

    // Constructor to initialize the ProductRepository
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Helper method to determine the timezone. Defaults to "UTC" if null or empty timezone is passed.
    private String getProductTimeZone(String productTimeZone) {
        if (productTimeZone == null || productTimeZone.isEmpty()) {
            return "UTC"; // return UTC if product timezone is empty
        }
        return productTimeZone;
    }

    // Helper method to get property names of a given object that are null
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        // Filter out properties that are null and return their names
        return Arrays.stream(pds)
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    // Method to create a new product
    @Override
    public ProductEntity createProduct(CreateProductDTO createProductDTO, String timeZone) throws ProductAlreadyFoundException {
        // Set the timezone
        timeZone = getProductTimeZone(timeZone);

        // Check if a product with the same name already exists
        if (productRepository.getProductByName(createProductDTO.getProductName()).isPresent()) {
            throw new ProductAlreadyFoundException("Product with the name you put already exists");
        }

        // Create a new product entity and populate it with data from the DTO
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(createProductDTO.getProductName());
        productEntity.setDescription(createProductDTO.getProductDescription());
        productEntity.setPrice(createProductDTO.getPrice());
        productEntity.setCurrency(createProductDTO.getCurrency());
        productEntity.setCategory(createProductDTO.getCategory());
        productEntity.setStock(createProductDTO.getStock());
        productEntity.setImage(createProductDTO.getImage());
        productEntity.setTags(createProductDTO.getTags());
        productEntity.setRating(0.0f); // Default rating when creating a new product
        productEntity.setCreatedAt(timeZone);
        productEntity.setUpdatedAt(timeZone);

        // Save and return the newly created product
        return productRepository.save(productEntity);
    }

    // Method to update an existing product
    @Override
    public ProductEntity updateProduct(UpdatedProductDTO updatedProductDTO, String timeZone) throws ProductNotFoundException {
        // Set the timezone
        timeZone = getProductTimeZone(timeZone);

        // Check if the product exists
        ProductEntity existingProduct = productRepository.getProductById(updatedProductDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("The product you're trying to update doesn't exist"));

        // Update the product's attributes using the non-null values from the DTO
        org.springframework.beans.BeanUtils.copyProperties(updatedProductDTO, existingProduct,
                getNullPropertyNames(updatedProductDTO));

        // Set the update timestamp
        existingProduct.setUpdatedAt(timeZone);

        // Save and return the updated product
        return productRepository.save(existingProduct);
    }

    // Method to delete an existing product
    @Override
    public ProductEntity deleteProduct(DeleteProductDTO deleteProductDTO, String timeZone) throws ProductNotFoundException {
        // Check if the product exists by its ID
        ProductEntity deletedProduct = productRepository.findById(deleteProductDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + deleteProductDTO.getId()));

        // Delete the product from the database
        productRepository.deleteById(deleteProductDTO.getId());

        // Return the deleted product entity
        return deletedProduct;
    }

    // Method to get a product by its ID
    @Override
    public Optional<ProductEntity> getProductByID(GetProductByIdDTO getProductByIdDTO) {
        // Fetch the product from the repository using its ID
        return productRepository.findById(getProductByIdDTO.getId());
    }

    // Method to get a product by its name
    @Override
    public Optional<ProductEntity> getProductByName(GetProductByNameDTO getProductByNameDTO) {
        // Fetch the product from the repository using its name
        return productRepository.getProductByName(getProductByNameDTO.getProductName());
    }

}
