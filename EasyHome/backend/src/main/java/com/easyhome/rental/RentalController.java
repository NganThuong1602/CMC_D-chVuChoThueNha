package com.easyhome.rental;
import com.easyhome.common.CurrentUser;
import com.easyhome.model.*;
import com.easyhome.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/requests")
public class RentalController {
 private final RentalRequestRepository requests;private final ListingRepository listings;private final CurrentUser current;
 public RentalController(RentalRequestRepository r,ListingRepository l,CurrentUser c) {requests=r;listings=l;current=c;}
 public record RequestInput(@NotNull Long listingId,@NotBlank @Size(max=1000) String message) {}
 public record StatusInput(@NotNull RequestStatus status) {}
 public record RequestDto(Long id,Long listingId,String listingTitle,Long renterId,String renterName,String renterEmail,
   String message,RequestStatus status,Instant createdAt) {
   static RequestDto from(RentalRequest r) {return new RequestDto(r.id,r.listing.id,r.listing.title,r.renter.id,
      r.renter.fullName,r.renter.email,r.message,r.status,r.createdAt);}
 }
 @PostMapping @Transactional
 public RequestDto create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody RequestInput input) {
   User user=current.get(jwt);
   if(user.role!=Role.RENTER) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chỉ khách thuê được gửi yêu cầu");
   Listing listing=listings.findById(input.listingId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy nhà"));
   if(listing.status!=ListingStatus.AVAILABLE) throw new IllegalArgumentException("Nhà không còn nhận yêu cầu thuê");
   if(requests.existsByListing_IdAndRenter_IdAndStatus(listing.id,user.id,RequestStatus.PENDING)) throw new IllegalArgumentException("Bạn đã gửi yêu cầu cho nhà này");
   RentalRequest req=new RentalRequest();req.listing=listing;req.renter=user;req.message=input.message().trim();
   return RequestDto.from(requests.save(req));
 }
 @GetMapping("/mine") @Transactional(readOnly=true)
 public List<RequestDto> mine(@AuthenticationPrincipal Jwt jwt) {
  User u=current.get(jwt);if(u.role!=Role.RENTER)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chỉ dành cho khách thuê");
  return requests.findByRenter_IdOrderByCreatedAtDesc(u.id).stream().map(RequestDto::from).toList();
 }
 @GetMapping("/received") @Transactional(readOnly=true)
 public List<RequestDto> received(@AuthenticationPrincipal Jwt jwt) {
  User u=current.get(jwt);if(u.role!=Role.OWNER)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chỉ dành cho chủ nhà");
  return requests.findByListing_Owner_IdOrderByCreatedAtDesc(u.id).stream().map(RequestDto::from).toList();
 }
 @PatchMapping("/{id}/status") @Transactional
 public RequestDto status(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id,@Valid @RequestBody StatusInput input) {
  User u=current.get(jwt);RentalRequest r=requests.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy yêu cầu"));
  if(u.role!=Role.OWNER||!r.listing.owner.id.equals(u.id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Không có quyền xử lý yêu cầu này");
  if(input.status()==RequestStatus.PENDING)throw new IllegalArgumentException("Không thể đặt lại trạng thái chờ");
  if(r.status!=RequestStatus.PENDING) throw new IllegalArgumentException("Yêu cầu đã được xử lý");
  if(input.status()==RequestStatus.ACCEPTED) {
    if(r.listing.status!=ListingStatus.AVAILABLE) throw new IllegalArgumentException("Nhà không còn trống");
    r.listing.status=ListingStatus.RENTED; listings.save(r.listing);
    for(RentalRequest other:requests.findByListing_IdAndStatus(r.listing.id,RequestStatus.PENDING)) {
      if(!other.id.equals(r.id)) { other.status=RequestStatus.REJECTED;requests.save(other); }
    }
  }
  r.status=input.status();return RequestDto.from(requests.save(r));
 }
}
