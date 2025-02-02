package com.W3yneRagsac.SnapShop.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Table(name = "Vendors")
@Entity
public class VendorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    @Column(name = "VendorName", nullable = false, unique = true)
    private String vendorName;

    @Column(unique = true, nullable = false)
    private String vendorEmail;

    @Column(unique = true)
    private String vendorPhoneNumber;

    @Column(unique = true, nullable = false)
    private String storeName;

    // Updated the mappedBy to match the actual field in ProductEntity
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ProductEntity> productEntities;
}
