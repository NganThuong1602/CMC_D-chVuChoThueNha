package com.easyhome.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(nullable=false) public String fullName;
  @Column(nullable=false, unique=true) public String email;
  @Column(nullable=false) public String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role;
  @Column(nullable=false) public Instant createdAt = Instant.now();
  public User() {}
  public User(String fullName, String email, String passwordHash, Role role) {
    this.fullName=fullName; this.email=email; this.passwordHash=passwordHash; this.role=role;
  }
}
