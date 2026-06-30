package com.payment.service.services;

import com.payment.service.model.Facture;
import com.payment.service.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;

    // Seeder les factures
    @Transactional
    public void seedFactures(List<String> walletCodes) {
        String[] services = {"ISM", "WOYAFAL"};
        LocalDate moisCourant = LocalDate.now().withDayOfMonth(1);

        for (String walletCode : walletCodes) {
            int walletId = Integer.parseInt(walletCode.replace("WLT-", "").replaceFirst("^0+", ""));

            for (int s = 0; s < services.length; s++) {
                String service = services[s];
                if (factureRepository.existsByWalletCodeAndServiceNameAndMois(
                        walletCode, service, moisCourant)) continue;

                Facture facture = new Facture();
                facture.setReference("FAC-" + service + "-" + walletId + "-" + (s + 1));
                facture.setWalletCode(walletCode);
                facture.setServiceName(service);
                facture.setAmount(service.equals("ISM") ? 150000.0 : 25000.0);
                facture.setStatus("UNPAID");
                facture.setMois(moisCourant);
                factureRepository.save(facture);
            }
        }
    }

    // Factures impayées du mois courant
    public List<Facture> getFacturesCourantes(String walletCode) {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = debut.plusMonths(1).minusDays(1);
        return factureRepository.findByWalletCodeAndStatusAndMoisBetween(
                walletCode, "UNPAID", debut, fin);
    }

    // Factures impayées du mois courant filtrées par service
    public List<Facture> getFacturesCourantesByService(String walletCode, String unite) {
        return factureRepository.findByWalletCodeAndServiceNameAndStatus(
                walletCode, unite, "UNPAID");
    }

    // Factures impayées sur une période
    public List<Facture> getFacturesByPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        return factureRepository.findByWalletCodeAndStatusAndMoisBetween(
                walletCode, "UNPAID", debut, fin);
    }

    // Payer la facture du mois courant pour un service
    @Transactional
    public List<Facture> payerFacturesMoisCourant(String walletCode, String serviceName) {
        List<Facture> factures = getFacturesCourantes(walletCode).stream()
                .filter(f -> f.getServiceName().equals(serviceName))
                .toList();

        if (factures.isEmpty())
            throw new RuntimeException("Aucune facture impayée trouvée pour " + serviceName);

        List<Facture> payees = new ArrayList<>();
        for (Facture f : factures) {
            f.setStatus("PAID");
            f.setPaidAt(LocalDateTime.now());
            payees.add(factureRepository.save(f));
        }
        return payees;
    }

    // Payer des factures spécifiques par référence
    @Transactional
    public List<Facture> payerFacturesSpecifiques(List<String> references) {
        List<Facture> payees = new ArrayList<>();
        for (String ref : references) {
            List<Facture> matches = factureRepository.findAllByReference(ref);
            if (matches.isEmpty())
                throw new RuntimeException("Facture non trouvée: " + ref);

            Facture f = matches.stream()
                    .filter(m -> "UNPAID".equals(m.getStatus()))
                    .findFirst()
                    .orElse(matches.get(0));

            f.setStatus("PAID");
            f.setPaidAt(LocalDateTime.now());
            payees.add(factureRepository.save(f));
        }
        return payees;
    }
}