package com.terra.team.prural.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPhone> listPhones;

    // Métodos para gestionar teléfonos con validación
    public boolean canAddPhone() {
        return listPhones == null || listPhones.size() < 3;
    }

    public void addPhone(UserPhone phone) {
        if (!canAddPhone()) {
            throw new IllegalStateException("No se puede agregar más de 3 números de teléfono por usuario");
        }
        if (listPhones == null) {
            listPhones = new ArrayList<>();
        }
        
        listPhones.add(phone);
        phone.setUser(this);
    }

    public void removePhone(UserPhone phone) {
        if (listPhones != null) {
            listPhones.remove(phone);
            phone.setUser(null);
        }
    }

    public int getPhoneCount() {
        return listPhones != null ? listPhones.size() : 0;
    }

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private boolean enabled = false;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "token_expiration_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpirationTime;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetPasswordTokenExpiration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private List<Role> roles;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public User() {}
    public User(String email, String password, String name, String lastname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return enabled; }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public List<UserPhone> getListPhones() { return listPhones; }
    public void setListPhones(List<UserPhone> listPhones) { this.listPhones = listPhones; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
    public Date getTokenExpirationTime() { return tokenExpirationTime; }
    public void setTokenExpirationTime(Date tokenExpirationTime) { this.tokenExpirationTime = tokenExpirationTime; }
    public String getResetPasswordToken() { return resetPasswordToken; }
    public void setResetPasswordToken(String resetPasswordToken) { this.resetPasswordToken = resetPasswordToken; }
    public Date getResetPasswordTokenExpiration() { return resetPasswordTokenExpiration; }
    public void setResetPasswordTokenExpiration(Date resetPasswordTokenExpiration) { this.resetPasswordTokenExpiration = resetPasswordTokenExpiration; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}
