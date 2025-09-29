package com.mongsom.dev.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_mst")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address2", nullable = false)
    private String address2;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "birth")
    private String birth;

    @Column(name = "agree_main")
    private Boolean agreeMain;

    @Column(name = "agree_shopping")
    private Boolean agreeShopping;

    @Column(name = "agree_sms")
    private Boolean agreeSms;

    @Column(name = "agree_email")
    private Boolean agreeEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider;
    
    @PrePersist
    public void prePersist() {
        if (this.provider == null) {
            this.provider = Provider.LOCAL;
        }
        if (this.status == null) {
            this.status = Status.ACTIVE;
        }
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    @Transactional
    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfile(String name, String phone, String email, String zipCode, String address, String address2, String birth) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.zipCode = zipCode;
        this.address = address;
        this.address2 = address2;
        this.birth = birth;
    }

    public void updateAgreements(Boolean agreeMain, Boolean agreeShopping, Boolean agreeSms, Boolean agreeEmail) {
        this.agreeMain = agreeMain;
        this.agreeShopping = agreeShopping;
        this.agreeSms = agreeSms;
        this.agreeEmail = agreeEmail;
    }
    
    // 탈퇴 시 userId 변경
    public void updateUserIdForWithdrawal(String withdrawnUserId) {
        this.userId = withdrawnUserId;
    }
    
    // 탈퇴 시 email 변경
    public void updateEmailForWithdrawal(String withdrawnEmail) {
        this.email = withdrawnEmail;
    }
}
