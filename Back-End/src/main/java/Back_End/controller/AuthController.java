package Back_End.controller;

import Back_End.dto.AuthDtos;
import Back_End.security.JwtService;
import Back_End.entity.User;
import Back_End.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;

  @PostMapping("/sign-in")
  public ResponseEntity<AuthDtos.AuthResponse> signIn(@Valid @RequestBody AuthDtos.SignUpRequest req) {
    User u = userService.register(req);
    String token = jwtService.generateToken(u.getUsername(), Map.of("uid", u.getId()));
    return ResponseEntity.ok(new AuthDtos.AuthResponse(token));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    User u = (User) auth.getPrincipal();
    String token = jwtService.generateToken(u.getUsername(), Map.of("uid", u.getId()));
    return ResponseEntity.ok(new AuthDtos.AuthResponse(token));
  }
}

