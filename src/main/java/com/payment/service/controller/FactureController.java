package com.payment.service.controller;

import com.payment.service.model.Facture;
import com.payment.service.services.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    // Seeder les factures
    @PostMapping("/seed")
    public ResponseEntity<?> seed(@RequestBody List<String> walletCodes) {
        factureService.seedFactures(walletCodes);
        return ResponseEntity.ok(Map.of("message", "Factures générées avec succès"));
    }

    // Factures impayées du mois courant
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<List<Facture>> getFacturesCourantes(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        if (unite != null) {
            return ResponseEntity.ok(factureService.getFacturesCourantesByService(walletCode, unite));
        }
        return ResponseEntity.ok(factureService.getFacturesCourantes(walletCode));
    }

    // Factures impayées sur une période
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<List<Facture>> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(factureService.getFacturesByPeriode(walletCode, debut, fin));
    }

    // Payer facture du mois courant
    @PostMapping("/pay/current")
    public ResponseEntity<List<Facture>> payerMoisCourant(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(factureService.payerFacturesMoisCourant(
                body.get("walletCode"), body.get("serviceName")));
    }

    // Payer factures spécifiques
    @PostMapping("/pay/specifiques")
    public ResponseEntity<List<Facture>> payerSpecifiques(@RequestBody Map<String, Object> body) {
        List<String> references = (List<String>) body.get("factureReferences");
        return ResponseEntity.ok(factureService.payerFacturesSpecifiques(references));
    }
}