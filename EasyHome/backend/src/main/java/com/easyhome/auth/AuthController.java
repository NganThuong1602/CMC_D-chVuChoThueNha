package com.easyhome.auth;
import com.easyhome.common.CurrentUser;
import com.easyhome.model.*;
import com.easyhome.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users;
 private final PasswordEncoder encoder;
 private final JwtEncoder jwtEncoder;
 private final CurrentUser current;
 public AuthController(UserRepository users, PasswordEncoder encoder, JwtEncoder jwtEncoder, CurrentUser current) {
   this.users=users;this.encoder=encoder;this.jwtEncoder=jwtEncoder;this.current=current;
 }
 public record RegisterDto(@NotBlank String fullName,@Email @NotBlank String email,
   @Size(min=6,max=100) String password,@NotNull Role role) {}
 public record LoginDto(@NotBlank String email,@NotBlank String password) {}
 public record UserDto(Long id,String fullName,String email,Role role) {
   public static UserDto from(User u) { return new UserDto(u.id,u.fullName,u.email,u.role); }
 }
 public record AuthDto(String token,UserDto user) {}
 @PostMapping("/register") public AuthDto register(@Valid @RequestBody RegisterDto req) {
   if (users.existsByEmailIgnoreCase(req.email().trim())) throw new IllegalArgumentException("Email đã được sử dụng");
   User u=users.save(new User(req.fullName().trim(),req.email().trim().toLowerCase(),encoder.encode(req.password()),req.role()));
   return auth(u);
 }
 @PostMapping("/login") public AuthDto login(@Valid @RequestBody LoginDto req) {
   User u=users.findByEmailIgnoreCase(req.email().trim()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sai email hoặc mật khẩu"));
   if(!encoder.matches(req.password(),u.passwordHash)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sai email hoặc mật khẩu");
   return auth(u);
 }
 @GetMapping("/me") public UserDto me(@AuthenticationPrincipal Jwt jwt) { return UserDto.from(current.get(jwt)); }
 private AuthDto auth(User u) {
   Instant now=Instant.now();
   JwtClaimsSet claims=JwtClaimsSet.builder().issuer("easyhome").issuedAt(now).expiresAt(now.plus(12,ChronoUnit.HOURS)).subject(u.id.toString()).claim("role",u.role.name()).build();
   JwsHeader header=JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
   return new AuthDto(jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue(),UserDto.from(u));
 }
}
