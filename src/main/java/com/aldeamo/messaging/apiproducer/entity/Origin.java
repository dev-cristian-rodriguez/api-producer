package com.aldeamo.messaging.apiproducer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "origins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Origin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
