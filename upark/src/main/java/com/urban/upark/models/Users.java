package com.urban.upark.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class Users implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Users;

    private String name;

    private String first_name;

    @Column(name="user_name")
    private String userName;

    private String email;

    private String password;

    private String phone_number;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CommissionPartners> commissionPartners;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Parking> parkings;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }


}
