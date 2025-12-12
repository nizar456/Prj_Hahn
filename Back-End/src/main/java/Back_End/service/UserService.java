package Back_End.service;

import Back_End.dto.AuthDtos;
import Back_End.entity.User;
import Back_End.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public User register(AuthDtos.SignUpRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new IllegalArgumentException("Email already in use");
    }
    User u = User.builder()
        .email(req.email())
        .password(passwordEncoder.encode(req.password()))
        .build();
    return userRepository.save(u);
  }
}

