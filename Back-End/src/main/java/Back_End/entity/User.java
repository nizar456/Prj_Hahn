package Back_End.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User implements UserDetails {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Email @NotBlank
  @Column(nullable = false)
  private String email;

  @NotBlank
  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude @JsonIgnore
  private List<Project> projects;

  // UserDetails
  @Override @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() { return List.of();  }//liste immuable vide
  @Override @JsonIgnore
  public String getUsername() { return email; }
  @Override @JsonIgnore
  public boolean isAccountNonExpired() { return true; }
  @Override @JsonIgnore
  public boolean isAccountNonLocked() { return true; }
  @Override @JsonIgnore
  public boolean isCredentialsNonExpired() { return true; }
  @Override @JsonIgnore
  public boolean isEnabled() { return true; }
}

