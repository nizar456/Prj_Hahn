package Back_End.service;

import Back_End.dto.TaskDtos;
import Back_End.entity.Task;
import Back_End.exception.NotFoundException;
import Back_End.entity.Project;
import Back_End.repository.ProjectRepository;
import Back_End.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;

  @Transactional(readOnly = true)
  public List<Task> list(Long projectId, Long ownerId) {
    ensureProjectOwned(projectId, ownerId);
    return taskRepository.findByProjectId(projectId);
  }

  @Transactional
  public Task create(Long projectId, Long ownerId, TaskDtos.TaskRequest req) {
    Project project = ensureProjectOwned(projectId, ownerId);
    Task t = Task.builder()
        .title(req.title())
        .description(req.description())
        .dueDate(req.dueDate())
        .completed(req.completed() != null && req.completed())
        .project(project)
        .build();
    return taskRepository.save(t);
  }

  @Transactional(readOnly = true)
  public Task get(Long projectId, Long taskId, Long ownerId) {
    ensureProjectOwned(projectId, ownerId);
    return taskRepository.findByIdAndProjectId(taskId, projectId)
        .orElseThrow(() -> new NotFoundException("Task not found"));
  }

  @Transactional
  public Task update(Long projectId, Long taskId, Long ownerId, TaskDtos.TaskRequest req) {
    Task t = get(projectId, taskId, ownerId);
    t.setTitle(req.title());
    t.setDescription(req.description());
    t.setDueDate(req.dueDate());
    if (req.completed() != null) t.setCompleted(req.completed());
    return taskRepository.save(t);
  }

  @Transactional
  public void delete(Long projectId, Long taskId, Long ownerId) {
    Task t = get(projectId, taskId, ownerId);
    taskRepository.delete(t);
  }

  private Project ensureProjectOwned(Long projectId, Long ownerId) {
    return projectRepository.findById(projectId)
        .filter(p -> p.getOwner().getId().equals(ownerId))
        .orElseThrow(() -> new NotFoundException("Project not found"));
  }

  public static TaskDtos.TaskResponse toResponse(Task t) {
    return new TaskDtos.TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getDueDate(), t.isCompleted());
  }
}

