package com.easyhome.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name="rental_requests")
public class RentalRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="listing_id",nullable=false) public Listing listing;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="renter_id",nullable=false) public User renter;
 @Column(nullable=false,length=1000) public String message;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public RequestStatus status=RequestStatus.PENDING;
 @Column(nullable=false) public Instant createdAt=Instant.now();
 public RentalRequest() {}
}
