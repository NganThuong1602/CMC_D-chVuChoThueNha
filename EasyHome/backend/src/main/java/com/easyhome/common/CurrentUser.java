package com.easyhome.common;
import com.easyhome.model.User;
import com.easyhome.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Component
public class CurrentUser {
 private final UserRepository users;
 public CurrentUser(UserRepository users) { this.users=users; }
 public User get(Jwt jwt) {
   if (jwt==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Bạn cần đăng nhập");
   try { return users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Tài khoản không tồn tại")); }
   catch (NumberFormatException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token không hợp lệ"); }
 }
}
