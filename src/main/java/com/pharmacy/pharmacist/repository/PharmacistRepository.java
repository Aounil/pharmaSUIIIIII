package com.pharmacy.pharmacist.repository;

import com.pharmacy.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacistRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndRole(String email, String role);
}
