package com.payment.service.repository;

import com.payment.service.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByWalletCodeAndStatusAndMoisBetween(
            String walletCode, String status, LocalDate debut, LocalDate fin);
    List<Facture> findByWalletCodeAndStatus(String walletCode, String status);
    List<Facture> findByWalletCodeAndServiceNameAndStatus(
            String walletCode, String serviceName, String status);
    Optional<Facture> findByReference(String reference);
    boolean existsByWalletCodeAndServiceNameAndMois(
            String walletCode, String serviceName, LocalDate mois);
}