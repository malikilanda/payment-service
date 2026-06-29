package com.payment.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reference; // FAC-ISM-3-1

    @Column(nullable = false)
    private String walletCode; // WLT-0000003

    @Column(nullable = false)
    private String serviceName; // ISM, WOYAFAL

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // UNPAID, PAID

    @Column(nullable = false)
    private LocalDate mois; // mois de la facture

    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "UNPAID";
    }
}