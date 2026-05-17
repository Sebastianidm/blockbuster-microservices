package com.blockbuster.catalog.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SecurityTestController {

    @GetMapping("/api/v1/catalog/secure-ping")
    public String securePing() {
        return "secured";
    }

    @PatchMapping("/api/v1/movies/1/stock/discount")
    public String internalDiscountPing() {
        return "discount";
    }

    @PatchMapping("/api/v1/movies/1/stock/restore")
    public String internalRestorePing() {
        return "restore";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/catalog/admin-ping")
    public String adminPing() {
        return "admin";
    }
}
