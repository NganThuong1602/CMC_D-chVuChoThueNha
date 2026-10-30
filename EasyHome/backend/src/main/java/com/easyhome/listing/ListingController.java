package com.easyhome.listing;
import com.easyhome.common.CurrentUser;
import com.easyhome.model.*;
import com.easyhome.repository.ListingRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/listings")
public class ListingController {
 private final ListingRepository listings; private final CurrentUser current;
 public ListingController(ListingRepository listings,CurrentUser current) { this.listings=listings;this.current=current; }
 public record ListingInput(@NotBlank String title,String description,@NotBlank String city,@NotBlank String district,
   @NotBlank String address,@NotBlank String type,@NotNull @DecimalMin("1") BigDecimal price,
   @NotNull @DecimalMin("1") Double area,@NotNull @Min(0) Integer bedrooms,String imageUrl) {}
 public record ListingDto(Long id,Long ownerId,String ownerName,String title,String description,String city,String district,
   String address,String type,BigDecimal price,Double area,Integer bedrooms,String imageUrl,ListingStatus status,Instant createdAt) {
   static ListingDto from(Listing l) { return new ListingDto(l.id,l.owner.id,l.owner.fullName,l.title,l.description,l.city,l.district,
    l.address,l.type,l.price,l.area,l.bedrooms,l.imageUrl,l.status,l.createdAt); }
 }
 @GetMapping @Transactional(readOnly=true)
 public List<ListingDto> all(@RequestParam(required=false) String q,@RequestParam(required=false) String city,
   @RequestParam(required=false) String type,@RequestParam(required=false) BigDecimal minPrice,
   @RequestParam(required=false) BigDecimal maxPrice) {
   Specification<Listing> spec=(root,query,cb)->{
     List<Predicate> ps=new ArrayList<>(); ps.add(cb.equal(root.get("status"),ListingStatus.AVAILABLE));
     if(q!=null&&!q.isBlank()) {String value="%"+q.trim().toLowerCase()+"%";ps.add(cb.or(cb.like(cb.lower(root.get("title")),value),cb.like(cb.lower(root.get("district")),value),cb.like(cb.lower(root.get("address")),value)));}
     if(city!=null&&!city.isBlank()) ps.add(cb.like(cb.lower(root.get("city")),"%"+city.trim().toLowerCase()+"%"));
     if(type!=null&&!type.isBlank()) ps.add(cb.equal(root.get("type"),type));
     if(minPrice!=null) ps.add(cb.greaterThanOrEqualTo(root.get("price"),minPrice));
     if(maxPrice!=null) ps.add(cb.lessThanOrEqualTo(root.get("price"),maxPrice));
     return cb.and(ps.toArray(new Predicate[0]));
   };
   return listings.findAll(spec,Sort.by(Sort.Direction.DESC,"createdAt")).stream().map(ListingDto::from).toList();
 }
 @GetMapping("/{id}") @Transactional(readOnly=true)
 public ListingDto one(@PathVariable Long id) {
   Listing l=find(id);if(l.status!=ListingStatus.AVAILABLE) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Tin không còn hiển thị");
   return ListingDto.from(l);
 }
 @GetMapping("/mine") @Transactional(readOnly=true)
 public List<ListingDto> mine(@AuthenticationPrincipal Jwt jwt) {
   User u=current.get(jwt);requireOwner(u);
   return listings.findByOwner_IdOrderByCreatedAtDesc(u.id).stream().map(ListingDto::from).toList();
 }
 @PostMapping @Transactional
 public ListingDto create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody ListingInput req) {
   User u=current.get(jwt);requireOwner(u);
   Listing l=new Listing(); l.owner=u;assign(l,req);return ListingDto.from(listings.save(l));
 }
 @PutMapping("/{id}") @Transactional
 public ListingDto update(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id,@Valid @RequestBody ListingInput req) {
   User u=current.get(jwt);Listing l=owned(id,u);
   if(l.status==ListingStatus.RENTED) throw new IllegalArgumentException("Nhà đã cho thuê, không thể sửa tin");
   assign(l,req);return ListingDto.from(listings.save(l));
 }
 @DeleteMapping("/{id}") @Transactional
 public ResponseEntity<Void> hide(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id) {
   Listing l=owned(id,current.get(jwt));l.status=ListingStatus.HIDDEN;listings.save(l);return ResponseEntity.noContent().build();
 }
 @PatchMapping("/{id}/restore") @Transactional
 public ListingDto restore(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id) {
   Listing l=owned(id,current.get(jwt));
   if(l.status!=ListingStatus.HIDDEN) throw new IllegalArgumentException("Chỉ có thể mở lại tin đã ẩn");
   l.status=ListingStatus.AVAILABLE;return ListingDto.from(listings.save(l));
 }
 private Listing find(Long id) {return listings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy nhà"));}
 private Listing owned(Long id,User user) {requireOwner(user);Listing l=find(id);if(!l.owner.id.equals(user.id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Không thể sửa tin của người khác");return l;}
 private void requireOwner(User user) {if(user.role!=Role.OWNER) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chỉ chủ nhà mới được dùng tính năng này");}
 private void assign(Listing l,ListingInput req) {
  l.title=req.title().trim();l.description=req.description()==null?"":req.description().trim();
  l.city=req.city().trim();l.district=req.district().trim();l.address=req.address().trim();
  l.type=req.type().trim();l.price=req.price();l.area=req.area();l.bedrooms=req.bedrooms();
  l.imageUrl=req.imageUrl()==null?"":req.imageUrl().trim();
 }
}
