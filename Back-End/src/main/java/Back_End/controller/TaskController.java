package Back_End.controller;

import Back_End.dto.TaskDtos;
import Back_End.service.TaskService;
import Back_End.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  public List<TaskDtos.TaskResponse> list(@AuthenticationPrincipal User user, @PathVariable Long projectId) {
    return taskService.list(projectId, user.getId()).stream().map(TaskService::toResponse).toList();
  }

  @PostMapping
  public ResponseEntity<TaskDtos.TaskResponse> create(@AuthenticationPrincipal User user,
                                                      @PathVariable Long projectId,
                                                      @Valid @RequestBody TaskDtos.TaskRequest req) {
    var created = taskService.create(projectId, user.getId(), req);
    return ResponseEntity.created(URI.create("/api/projects/%d/tasks/%d".formatted(projectId, created.getId())))
        .body(TaskService.toResponse(created));
  }

  @GetMapping("/{taskId}")
  public TaskDtos.TaskResponse get(@AuthenticationPrincipal User user,
                                   @PathVariable Long projectId,
                                   @PathVariable Long taskId) {
    return TaskService.toResponse(taskService.get(projectId, taskId, user.getId()));
  }

  @PutMapping("/{taskId}")
  public TaskDtos.TaskResponse update(@AuthenticationPrincipal User user,
                                      @PathVariable Long projectId,
                                      @PathVariable Long taskId,
                                      @Valid @RequestBody TaskDtos.TaskRequest req) {
    return TaskService.toResponse(taskService.update(projectId, taskId, user.getId(), req));
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> delete(@AuthenticationPrincipal User user,
                                     @PathVariable Long projectId,
                                     @PathVariable Long taskId) {
    taskService.delete(projectId, taskId, user.getId());
    return ResponseEntity.noContent().build();
  }
}
