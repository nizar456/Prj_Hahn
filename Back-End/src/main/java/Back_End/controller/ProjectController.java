package Back_End.controller;

import Back_End.dto.ProjectDtos;
import Back_End.service.ProjectService;
import Back_End.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping
  public List<ProjectDtos.ProjectResponse> list(@AuthenticationPrincipal User user) {
    return projectService.listForUser(user.getId()).stream()
        .map(projectService::toResponse).toList();
  }

  @PostMapping
  public ResponseEntity<ProjectDtos.ProjectResponse> create(@AuthenticationPrincipal User user,
                                                            @Valid @RequestBody ProjectDtos.ProjectRequest req) {
    var created = projectService.create(user.getId(), req);
    return ResponseEntity.created(URI.create("/api/projects/" + created.getId()))
        .body(projectService.toResponse(created));
  }

  @GetMapping("/{projectId}")
  public ProjectDtos.ProjectResponse get(@AuthenticationPrincipal User user, @PathVariable Long projectId) {
    return projectService.toResponse(projectService.getOwned(projectId, user.getId()));
  }

  @PutMapping("/{projectId}")
  public ProjectDtos.ProjectResponse update(@AuthenticationPrincipal User user,
                                            @PathVariable Long projectId,
                                            @Valid @RequestBody ProjectDtos.ProjectRequest req) {
    return projectService.toResponse(projectService.update(projectId, user.getId(), req));
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long projectId) {
    projectService.delete(projectId, user.getId());
    return ResponseEntity.noContent().build();
  }
}
