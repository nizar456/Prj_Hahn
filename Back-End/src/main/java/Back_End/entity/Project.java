package Back_End.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(min = 3, max = 120)
  @Column(nullable = false, length = 120)
  private String title;

  @Size(max = 1000)
  @Column(length = 1000)
  private String description;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  @ToString.Exclude @JsonIgnore //éviter les boucles infinies
  private User owner;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default //pour initialiser la liste même si le constructeur builder est utilisé
  private List<Task> tasks = new ArrayList<>();

  @Transient //ne sera pas persisté dans la base de données
  public float getProgress() {
    if (tasks == null || tasks.isEmpty()) return 0f;
    long completed = tasks.stream().filter(Task::isCompleted).count();
    return (float) (completed * 100.0 / tasks.size());
  }
}

