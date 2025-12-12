package Back_End.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectDtos {
  public record ProjectRequest(
      @NotBlank @Size(min = 3, max = 120) String title,
      @Size(max = 1000) String description
  ) {}

  public record ProjectResponse(
      Long id, String title, String description, float progress
  ) {}
}

