package com.rocketFoodDelivery.rocketFood.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user entity with security details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the user

    private String name; // Name of the user

    @Column(unique = true)
    private String email; // Email of the user, must be unique

    private String password; // Password of the user

    @Transient
    private boolean isEmployee; // Transient field to check if the user is an employee

    private Integer customerId; // ID of the customer, if applicable
    private Integer courierId; // ID of the courier, if applicable

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = true) // Allow nullable for restaurant_id
    @JsonManagedReference
    private Restaurant restaurant; // Restaurant associated with the user

    /**
     * Returns the authorities granted to the user.
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (isEmployee) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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

}
