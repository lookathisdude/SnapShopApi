package com.W3yneRagsac.SnapShop.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Products")
@Entity
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productDescription;

    @Column(nullable = false)
    private Double productPrice;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false)
    private Integer productStock;

    @Column(nullable = false)
    private List<String> productImage;

    @ElementCollection
    private List<String> productTags;

    @Column(nullable = false)
    private Float productRating;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // Reference to the vendor entity
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "vendorId")
    private VendorEntity vendor;
}
