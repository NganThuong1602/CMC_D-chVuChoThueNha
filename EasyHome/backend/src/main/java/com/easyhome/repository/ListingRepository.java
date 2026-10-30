package com.easyhome.repository;
import com.easyhome.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
public interface ListingRepository extends JpaRepository<Listing,Long>, JpaSpecificationExecutor<Listing> {
 List<Listing> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
}
