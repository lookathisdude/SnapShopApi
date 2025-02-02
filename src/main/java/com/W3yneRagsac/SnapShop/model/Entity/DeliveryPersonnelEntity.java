package com.W3yneRagsac.SnapShop.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table(name="DeliveryPersonnels")
@Entity
public class DeliveryPersonnelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryPersonnelId; // Changed from delivery_PersonnelId to deliveryPersonnelId (camelCase)

    @Column(name = "deliveryPersonnelName", unique = true, nullable = false)
    private String deliveryPersonnelName; // Changed from delivery_PersonnelName to deliveryPersonnelName (camelCase)

    @Column(name = "deliveryPersonnelNumber", unique = true)
    private String deliveryPersonnelNumber; // Changed from delivery_PersonnelNumber to deliveryPersonnelNumber (camelCase)

    @Column(name = "vehicleType")
    private String vehicleType; // Changed to private

    @Column(name = "deliverySchedule") // Removed unique=true if it's not necessary
    private String deliverySchedule;

}
