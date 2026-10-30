package com.easyhome.config;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration @EnableWebSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secret) {
   return new NimbusJwtEncoder(new ImmutableSecret<>(secret.getBytes(StandardCharsets.UTF_8)));
 }
 @Bean JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
   SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
   return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource() {
   CorsConfiguration c=new CorsConfiguration();
   c.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
   c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
   c.setAllowedHeaders(List.of("Authorization","Content-Type"));
   UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
   source.registerCorsConfiguration("/**",c); return source;
 }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
   return http.csrf(csrf->csrf.disable()).cors(cors->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
     .headers(h->h.frameOptions(f->f.sameOrigin()))
     .authorizeHttpRequests(a->a
       .requestMatchers("/api/auth/login","/api/auth/register", "/error", "/h2-console/**").permitAll()
       .requestMatchers(HttpMethod.GET,"/api/listings/mine").authenticated()
       .requestMatchers(HttpMethod.GET,"/api/listings","/api/listings/*").permitAll()
       .anyRequest().authenticated())
     .oauth2ResourceServer(o->o.jwt(j->{})).build();
 }
}
