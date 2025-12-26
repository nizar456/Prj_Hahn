package Back_End.service;

import Back_End.dto.ProjectDtos;
import Back_End.entity.Project;
import Back_End.exception.NotFoundException;
import Back_End.entity.User;
import Back_End.repository.ProjectRepository;
import Back_End.repository.UserRepository;
import Back_End.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;

  @Transactional(readOnly = true)
  public List<Project> listForUser(Long ownerId) {
    return projectRepository.findByOwnerId(ownerId);
  }

  @Transactional
  public Project create(Long ownerId, ProjectDtos.ProjectRequest req) {
    User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Owner not found"));
    Project p = Project.builder()
        .title(req.title())
        .description(req.description())
        .owner(owner)
        .build();
    return projectRepository.save(p);
  }

  @Transactional(readOnly = true)
  public Project getOwned(Long projectId, Long ownerId) {
    return projectRepository.findByIdAndOwnerId(projectId, ownerId)
        .orElseThrow(() -> new NotFoundException("Project not found"));
  }

  @Transactional
  public Project update(Long projectId, Long ownerId, ProjectDtos.ProjectRequest req) {
    Project p = getOwned(projectId, ownerId);
    p.setTitle(req.title());
    p.setDescription(req.description());
    return projectRepository.save(p);
  }

  @Transactional
  public void delete(Long projectId, Long ownerId) {
    Project p = getOwned(projectId, ownerId);
    projectRepository.delete(p);
  }

  public static ProjectDtos.ProjectResponse toResponse(Project p, TaskRepository taskRepository) {
    long total = taskRepository.countByProjectId(p.getId());
    long completed = total == 0 ? 0 : taskRepository.countByProjectIdAndCompletedTrue(p.getId());
    float progress = total == 0 ? 0f : (float) (completed * 100.0 / total);
    return new ProjectDtos.ProjectResponse(p.getId(), p.getTitle(), p.getDescription(), progress);
  }

  public ProjectDtos.ProjectResponse toResponse(Project p) {
    long total = taskRepository.countByProjectId(p.getId());
    long completed = total == 0 ? 0 : taskRepository.countByProjectIdAndCompletedTrue(p.getId());
    float progress = total == 0 ? 0f : (float) (completed * 100.0 / total);
    return new ProjectDtos.ProjectResponse(p.getId(), p.getTitle(), p.getDescription(), progress);
  }
}
