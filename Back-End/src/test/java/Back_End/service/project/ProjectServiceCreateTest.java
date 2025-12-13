package Back_End.service.project;

import Back_End.dto.ProjectDtos;
import Back_End.entity.Project;
import Back_End.entity.User;
import Back_End.exception.NotFoundException;
import Back_End.repository.ProjectRepository;
import Back_End.repository.TaskRepository;
import Back_End.repository.UserRepository;
import Back_End.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService - create() method tests")
class ProjectServiceCreateTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private User owner;
    private ProjectDtos.ProjectRequest validRequest;

    @BeforeEach
    void setUp() {
        // Préparer un utilisateur valide
        owner = User.builder()
                .id(1L)
                .email("owner@test.com")
                .password("password123")
                .build();

        // Préparer une requête valide
        validRequest = new ProjectDtos.ProjectRequest("Test Project", "Test Description");
    }

    @Test
    @DisplayName("create() - Happy path: should create project with correct attributes when owner exists")
    void create_WhenOwnerExists_ShouldReturnProjectWithCorrectAttributes() {
        // Arrange
        Long ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        // Simuler le save qui retourne le projet avec un ID généré
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0);
            savedProject.setId(100L); // Simuler l'ID généré par la base
            return savedProject;
        });

        // Act
        Project result = projectService.create(ownerId, validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo(validRequest.title());
        assertThat(result.getDescription()).isEqualTo(validRequest.description());
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getOwner().getId()).isEqualTo(ownerId);

        // Vérifier que le repository a été appelé avec les bons arguments
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());

        Project capturedProject = projectCaptor.getValue();
        assertThat(capturedProject.getTitle()).isEqualTo("Test Project");
        assertThat(capturedProject.getDescription()).isEqualTo("Test Description");
        assertThat(capturedProject.getOwner()).isEqualTo(owner);

        // Vérifier les interactions
        verify(userRepository, times(1)).findById(ownerId);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("create() - Failure: should throw NotFoundException when owner does not exist")
    void create_WhenOwnerDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        Long invalidOwnerId = 999L;

        when(userRepository.findById(invalidOwnerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.create(invalidOwnerId, validRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Owner not found");

        // Vérifier que le repository findById a été appelé
        verify(userRepository, times(1)).findById(invalidOwnerId);

        // Vérifier que save n'a JAMAIS été appelé (car l'exception est levée avant)
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("create() - Happy path: should create project with null description")
    void create_WhenDescriptionIsNull_ShouldCreateProjectSuccessfully() {
        // Arrange
        Long ownerId = 1L;
        ProjectDtos.ProjectRequest requestWithNullDescription =
                new ProjectDtos.ProjectRequest("Project Without Description", null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0);
            savedProject.setId(101L);
            return savedProject;
        });

        // Act
        Project result = projectService.create(ownerId, requestWithNullDescription);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Project Without Description");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getOwner()).isEqualTo(owner);
    }
}

