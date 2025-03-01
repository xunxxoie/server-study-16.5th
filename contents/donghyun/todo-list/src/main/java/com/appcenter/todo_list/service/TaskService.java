package com.appcenter.todo_list.service;

import com.appcenter.todo_list.dto.request.TaskRequestDto;
import com.appcenter.todo_list.dto.response.TaskResponseDto;
import com.appcenter.todo_list.entity.Category;
import com.appcenter.todo_list.entity.Task;
import com.appcenter.todo_list.entity.User;
import com.appcenter.todo_list.exception.CustomException;
import com.appcenter.todo_list.exception.ErrorCode;
import com.appcenter.todo_list.repository.CategoryRepository;
import com.appcenter.todo_list.repository.TaskRepository;
import com.appcenter.todo_list.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        return TaskResponseDto.entityToDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByCategoryId(Long categoryId) {
        List<Task> findTasks = taskRepository.findByCategoryId(categoryId);

        return findTasks.stream().map(TaskResponseDto::entityToDto).collect(
                Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByUserId(Long userId) {
        List<Task> findTasks = taskRepository.findByUserId(userId);

        return findTasks.stream().map(TaskResponseDto::entityToDto).collect(
                Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        List<Task> findTasks = taskRepository.findAll();

        return findTasks.stream().map(TaskResponseDto::entityToDto).collect(Collectors.toList());
    }

    public TaskResponseDto createTask(Long userId, TaskRequestDto taskRequestDto) {
        Category category = categoryRepository.findById(taskRequestDto.getCategoryId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Task task = TaskRequestDto.dtoToEntity(taskRequestDto, category, user);

        Task savedTask = taskRepository.save(task);

        return TaskResponseDto.entityToDto(savedTask);
    }

    public TaskResponseDto updateTask(Long taskId, TaskRequestDto taskRequestDto) {
        Category category = categoryRepository.findById(taskRequestDto.getCategoryId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        Task updatedTask = task.update(taskRequestDto, category);

        return TaskResponseDto.entityToDto(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        taskRepository.delete(task);
    }


    public List<TaskResponseDto> getTask(Long categoryId, Long userId, Long taskId) {
        if (categoryId == 0 && userId == 0 && taskId == 0) {
            return getAllTasks();
        } else if (categoryId == 0 && userId == 0 && taskId > 0) {
            return getTasksByCategoryId(taskId);
        } else if (categoryId == 0) {
            return getTasksByUserId(userId);
        } else if (userId == 0) {
            return getTasksByCategoryId(categoryId);
        }

        List<Task> task = taskRepository.findByCategoryIdAndUserId(categoryId, userId);
        return task.stream().map(TaskResponseDto::entityToDto).collect(Collectors.toList());
    }
}
