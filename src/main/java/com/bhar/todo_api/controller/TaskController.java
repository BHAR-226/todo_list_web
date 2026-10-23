package com.bhar.todo_api.controller;

import com.bhar.todo_api.models.Task;
import com.bhar.todo_api.models.User;
import com.bhar.todo_api.services.TaskServices;
import com.bhar.todo_api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServices services;
    private final UserService userService;

    public TaskController(TaskServices services, UserService userService){
        this.services = services;
        this.userService = userService;
    }

    private User currentUser(Authentication authentication){
        if (authentication == null || authentication.getName() == null) {return null;}
        return userService.findByName(authentication.getName());
    }

    @GetMapping()
    public List<Task> getTasks(Authentication authentication) {
        User user = currentUser(authentication);
        if (user == null) {return List.of();}
        return services.taskList(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask (@PathVariable int id, Authentication authentication){
        Task task = services.getTask(currentUser(authentication), id);
        if (task == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(task);
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication authentication){
        User user = currentUser(authentication);
        if (user == null) {return ResponseEntity.status(401).build();}
        Task created = services.newTask(
                user,
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority()
        );
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Task> editTask (@PathVariable int id, @RequestBody Task task,
                                          Authentication authentication){
        Task updated = services.modifyTask(
                currentUser(authentication),
                id,
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority()
        );
        if (updated == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> executeTask (@PathVariable int id, Authentication authentication){
        if (!services.execute(currentUser(authentication), id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<Void> unexecuteTask (@PathVariable int id, Authentication authentication){
        if (!services.unexecute(currentUser(authentication), id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask (@PathVariable int id, Authentication authentication){
        if (!services.deleteTask(currentUser(authentication), id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
