package com.W3yneRagsac.SnapShop.resolvers;

import com.W3yneRagsac.SnapShop.DTO.Product.CreateProductDTO;
import com.W3yneRagsac.SnapShop.DTO.Product.UpdatedProductDTO;
import com.W3yneRagsac.SnapShop.exceptions.ProductAlreadyFoundException;
import com.W3yneRagsac.SnapShop.exceptions.ProductNotFoundException;
import com.W3yneRagsac.SnapShop.model.ProductEntity;
import com.W3yneRagsac.SnapShop.service.classes.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Controller
public class ProductResolver {

    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(UserResolver.class);

    // Query(get) requests
    @QueryMapping
    @PreAuthorize("hasAnyRole('GUEST, CUSTOMER, VENDOR')'")
    public ProductEntity getProductById(@Argument("input") Long productId) throws ProductNotFoundException {
        // Ensure productId is valid
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null.");
        }

        logger.info("Fetching the product with ID: {}", productId);

        // Fetch the product using the service
        return productService.getProductByID(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('GUEST, CUSTOMER, VENDOR')'")
    public ProductEntity getProductByName(@Argument("input") String productName) throws ProductNotFoundException {
        // Ensure productId is valid
        if (productName == null) {
            throw new IllegalArgumentException("Product ID cannot be null.");
        }

        logger.info("Fetching the product with name: {}", productName);

        // Fetch the product using the service
        return productService.getProductByName(productName)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productName + " not found."));
    }

    // Mutation mapping (handles the other requests)
    @MutationMapping
    @PreAuthorize("hasRole('VENDOR'")
    public ProductEntity createProduct(@Argument("input") CreateProductDTO createProductDTO, String timeZone,
                                       BindingResult bindingResult) {
        // Check for validation errors
        if(bindingResult.hasErrors()) {
            // Collect all validation error messages
            StringBuilder errorMessage = new StringBuilder("Validation failed: "); // it will show validation failed when it has errors
            bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
            throw new IllegalArgumentException(errorMessage.toString());
        }
        try {
            return productService.createProduct(createProductDTO, timeZone);
        } catch (ProductAlreadyFoundException e) {
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }

//    TODO: IMPLEMENT THE ROLES
//    @MutationMapping
//    @PreAuthorize("hasRole('Seller'")
//    public ProductEntity updateProduct(@Argument("input")UpdatedProductDTO updatedProductDTO, String timeZone,
//    BindingResult bindingResult) {
//        // Check for validation errors
//        if(bindingResult.hasErrors()) {
//            // Collect all validation error messages
//            StringBuilder errorMessage = new StringBuilder("Validation failed: "); // it will show validation failed when it has errors
//            bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
//            throw new IllegalArgumentException(errorMessage.toString());
//        }
//    }
}
