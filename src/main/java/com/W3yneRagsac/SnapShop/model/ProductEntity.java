package com.W3yneRagsac.SnapShop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Use Long for ID

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private String currency;

    private String category;

    @Column(nullable = false)
    private Integer stock;

    @ElementCollection
    private List<String> image;

    @ElementCollection
    private List<String> tags;

    private Float rating;

// TODO: Implement the reviews
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private List<Review> reviews;  // Assuming you have a Review entity

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

// TODO: Implement the sellers
//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private Seller seller;  // Assuming you have a Seller entity

    @ManyToMany
    @JoinTable(
            name = "product_related_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "related_product_id")
    )
    private List<ProductEntity> relatedProducts;
}
