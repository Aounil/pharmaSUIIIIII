package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PharmacistPrincipalTest {

    @Test
    void userDetailsMethods_shouldExposePharmacistIdentity() {
        PharmacistPrincipal principal = new PharmacistPrincipal(
                1L,
                "pharmacist@example.com",
                "ROLE_PHARMACIST",
                20L
        );

        assertThat(principal.getId()).isEqualTo(1L);
        assertThat(principal.getPharmacyId()).isEqualTo(20L);
        assertThat(principal.getUsername()).isEqualTo("pharmacist@example.com");
        assertThat(principal.getPassword()).isNull();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PHARMACIST");
        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isTrue();
    }

    @Test
    void equals_shouldReturnTrueForSameInstanceAndSameValues() {
        PharmacistPrincipal principal = new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 20L);
        PharmacistPrincipal sameValues = new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 20L);

        assertThat(principal).isEqualTo(principal);
        assertThat(principal).isEqualTo(sameValues);
        assertThat(principal.hashCode()).isEqualTo(sameValues.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentTypeOrDifferentFields() {
        PharmacistPrincipal principal = new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 20L);

        assertThat(principal).isNotEqualTo("not a principal");
        assertThat(principal).isNotEqualTo(new PharmacistPrincipal(2L, "pharmacist@example.com", "ROLE_PHARMACIST", 20L));
        assertThat(principal).isNotEqualTo(new PharmacistPrincipal(1L, "other@example.com", "ROLE_PHARMACIST", 20L));
        assertThat(principal).isNotEqualTo(new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_ADMIN", 20L));
        assertThat(principal).isNotEqualTo(new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 21L));
    }
}
