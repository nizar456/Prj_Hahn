package Back_End.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(min = 3, max = 200)
  @Column(nullable = false, length = 200)
  private String title;

  @Size(max = 2000)
  @Column(length = 2000)
  private String description;

  @FutureOrPresent //valider que la date est aujourd'hui ou dans le futur
  private LocalDate dueDate;

  @Column(nullable = false)
  private boolean completed;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  @ToString.Exclude @JsonIgnore
  private Project project;
}

