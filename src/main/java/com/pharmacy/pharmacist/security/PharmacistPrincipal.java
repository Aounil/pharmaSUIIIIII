package com.pharmacy.pharmacist.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PharmacistPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String role;
    private final Long pharmacyId;

    public PharmacistPrincipal(Long id, String email, String role, Long pharmacyId) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }

    public Long getId() {
        return id;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PharmacistPrincipal)) return false;
        PharmacistPrincipal that = (PharmacistPrincipal) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(role, that.role) && Objects.equals(pharmacyId, that.pharmacyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, role, pharmacyId);
    }
}
