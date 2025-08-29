package com.terra.team.prural.auth.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_phones")
public class UserPhone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type")
    private PhoneType phoneType;
    
    @Column(name = "is_primary")
    private boolean isPrimary;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // Constructors
    public UserPhone() {}
    
    public UserPhone(String phoneNumber, User user) {
        this.phoneNumber = phoneNumber;
        this.user = user;
    }
    
    public UserPhone(String phoneNumber, User user, PhoneType phoneType, boolean isPrimary) {
        this.phoneNumber = phoneNumber;
        this.user = user;
        this.phoneType = phoneType;
        this.isPrimary = isPrimary;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public PhoneType getPhoneType() {
        return phoneType;
    }
    
    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }
    
    public boolean isPrimary() {
        return isPrimary;
    }
    
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
