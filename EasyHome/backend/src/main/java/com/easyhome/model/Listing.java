package com.easyhome.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name="listings")
public class Listing {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="owner_id",nullable=false) public User owner;
 @Column(nullable=false,length=160) public String title;
 @Column(length=3000) public String description;
 @Column(nullable=false) public String city;
 @Column(nullable=false) public String district;
 @Column(nullable=false) public String address;
 @Column(nullable=false) public String type;
 @Column(nullable=false,precision=15,scale=0) public BigDecimal price;
 @Column(nullable=false) public Double area;
 @Column(nullable=false) public Integer bedrooms;
 @Column(length=1000) public String imageUrl;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public ListingStatus status=ListingStatus.AVAILABLE;
 @Column(nullable=false) public Instant createdAt=Instant.now();
 public Listing() {}
}
