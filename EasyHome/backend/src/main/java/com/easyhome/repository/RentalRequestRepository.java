package com.easyhome.repository;
import com.easyhome.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RentalRequestRepository extends JpaRepository<RentalRequest,Long> {
 List<RentalRequest> findByRenter_IdOrderByCreatedAtDesc(Long renterId);
 List<RentalRequest> findByListing_Owner_IdOrderByCreatedAtDesc(Long ownerId);
 List<RentalRequest> findByListing_IdAndStatus(Long listingId, RequestStatus status);
 boolean existsByListing_IdAndRenter_IdAndStatus(Long listingId, Long renterId, RequestStatus status);
}
