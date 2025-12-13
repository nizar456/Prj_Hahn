package Back_End.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskDtos {
  public record TaskRequest(
      @NotBlank @Size(min = 3, max = 200) String title,
      @Size(max = 2000) String description,
      @FutureOrPresent LocalDate dueDate,
      Boolean completed
  ) {}

  public record TaskResponse(
      Long id, String title, String description, LocalDate dueDate, boolean completed
  ) {}
}

