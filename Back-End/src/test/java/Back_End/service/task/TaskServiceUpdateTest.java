package Back_End.service.task;

import Back_End.dto.TaskDtos;
import Back_End.entity.Project;
import Back_End.entity.Task;
import Back_End.entity.User;
import Back_End.exception.NotFoundException;
import Back_End.repository.ProjectRepository;
import Back_End.repository.TaskRepository;
import Back_End.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - update() method tests")
class TaskServiceUpdateTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private Project project;
    private Task existingTask;

    private static final Long PROJECT_ID = 1L;
    private static final Long TASK_ID = 10L;
    private static final Long OWNER_ID = 100L;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(OWNER_ID)
                .email("owner@test.com")
                .password("password123")
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .title("Test Project")
                .description("Test Project Description")
                .owner(owner)
                .build();

        existingTask = Task.builder()
                .id(TASK_ID)
                .title("Original Title")
                .description("Original Description")
                .dueDate(LocalDate.of(2025, 12, 20))
                .completed(false)
                .project(project)
                .build();
    }

    @Nested
    @DisplayName("Update success tests")
    class UpdateSuccessTests {

        @Test
        @DisplayName("update() - Happy path: should update all fields correctly")
        void update_WhenValidRequest_ShouldUpdateAllFields() {
            // Arrange
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Updated Title",
                    "Updated Description",
                    LocalDate.of(2025, 12, 25),
                    true
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(taskRepository.findByIdAndProjectId(TASK_ID, PROJECT_ID)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Task result = taskService.update(PROJECT_ID, TASK_ID, OWNER_ID, updateRequest);

            // Assert
            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getDescription()).isEqualTo("Updated Description");
            assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 25));
            assertThat(result.isCompleted()).isTrue();

            // Vérifier que save a été appelé avec la tâche mise à jour
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());

            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getTitle()).isEqualTo("Updated Title");
            assertThat(savedTask.getDescription()).isEqualTo("Updated Description");
            assertThat(savedTask.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 25));
            assertThat(savedTask.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("update() - should update title only and keep other fields")
        void update_WhenOnlyTitleChanged_ShouldUpdateCorrectly() {
            // Arrange
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "New Title Only",
                    "Original Description",
                    LocalDate.of(2025, 12, 20),
                    null // completed is null, should keep original value
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(taskRepository.findByIdAndProjectId(TASK_ID, PROJECT_ID)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Task result = taskService.update(PROJECT_ID, TASK_ID, OWNER_ID, updateRequest);

            // Assert
            assertThat(result.getTitle()).isEqualTo("New Title Only");
            assertThat(result.getDescription()).isEqualTo("Original Description");
            assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 20));
            assertThat(result.isCompleted()).isFalse(); // Should remain false (original value)
        }

        @Test
        @DisplayName("update() - should set completed to false when request has false")
        void update_WhenCompletedSetToFalse_ShouldUpdateCorrectly() {
            // Arrange
            existingTask.setCompleted(true); // Task was completed

            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Title",
                    "Description",
                    LocalDate.of(2025, 12, 25),
                    false // Set to not completed
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(taskRepository.findByIdAndProjectId(TASK_ID, PROJECT_ID)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Task result = taskService.update(PROJECT_ID, TASK_ID, OWNER_ID, updateRequest);

            // Assert
            assertThat(result.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("update() - should handle null description correctly")
        void update_WhenDescriptionIsNull_ShouldSetNullDescription() {
            // Arrange
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Updated Title",
                    null,
                    LocalDate.of(2025, 12, 25),
                    true
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(taskRepository.findByIdAndProjectId(TASK_ID, PROJECT_ID)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Task result = taskService.update(PROJECT_ID, TASK_ID, OWNER_ID, updateRequest);

            // Assert
            assertThat(result.getDescription()).isNull();
        }

    }

    @Nested
    @DisplayName("Update failure tests - NotFoundException")
    class UpdateNotFoundExceptionTests {

        @Test
        @DisplayName("update() - should throw NotFoundException when task does not exist")
        void update_WhenTaskNotFound_ShouldThrowNotFoundException() {
            // Arrange
            Long nonExistentTaskId = 999L;
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Updated Title",
                    "Updated Description",
                    LocalDate.of(2025, 12, 25),
                    true
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(taskRepository.findByIdAndProjectId(nonExistentTaskId, PROJECT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.update(PROJECT_ID, nonExistentTaskId, OWNER_ID, updateRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Task not found");

            // Vérifier que save n'a jamais été appelé
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("update() - should throw NotFoundException when project does not exist")
        void update_WhenProjectNotFound_ShouldThrowNotFoundException() {
            // Arrange
            Long nonExistentProjectId = 888L;
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Updated Title",
                    "Updated Description",
                    LocalDate.of(2025, 12, 25),
                    true
            );

            when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.update(nonExistentProjectId, TASK_ID, OWNER_ID, updateRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Project not found");

            // Vérifier que les repositories n'ont pas été appelés pour la suite
            verify(taskRepository, never()).findByIdAndProjectId(any(), any());
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        @DisplayName("update() - should throw NotFoundException when owner does not match project owner")
        void update_WhenOwnerDoesNotMatch_ShouldThrowNotFoundException() {
            // Arrange
            Long differentOwnerId = 999L; // Different from project owner
            TaskDtos.TaskRequest updateRequest = new TaskDtos.TaskRequest(
                    "Updated Title",
                    "Updated Description",
                    LocalDate.of(2025, 12, 25),
                    true
            );

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

            // Act & Assert
            assertThatThrownBy(() -> taskService.update(PROJECT_ID, TASK_ID, differentOwnerId, updateRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Project not found");

            // Vérifier que save n'a jamais été appelé
            verify(taskRepository, never()).save(any(Task.class));
        }
    }
}

