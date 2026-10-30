package com.easyhome.common;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException e) {
   String message=e.getBindingResult().getFieldErrors().stream().findFirst()
     .map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Dữ liệu không hợp lệ");
   return ResponseEntity.badRequest().body(Map.of("message",message));
 }
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<Map<String,String>> illegal(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<Map<String,String>> http(ResponseStatusException e) {
   return ResponseEntity.status(e.getStatusCode()).body(Map.of("message",e.getReason()==null?"Lỗi yêu cầu":e.getReason()));
 }
}
