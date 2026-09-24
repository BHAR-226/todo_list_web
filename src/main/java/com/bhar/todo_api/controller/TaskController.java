package com.bhar.todo_api.controller;
import com.bhar.todo_api.models.Task;
import com.bhar.todo_api.services.TaskServices;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServices services;

    public TaskController(TaskServices services){
        this.services = services;
    }

    @GetMapping()
    public List<Task> getTasks() {
        return  services.taskList() ;

    }

    @GetMapping("/{id}")
    public Task getTask (@PathVariable int id){
        return services.getTask(id);
    }

    @PostMapping()
    public Task createTask(@RequestBody Task task){
        return services.newTask(
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority()
        );
    }

    @PutMapping ("/{id}")
    public Task editTask (@PathVariable int id, @RequestBody Task task){
        return services.modifyTask(
                id,
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority()
        );
    }

    @PatchMapping("/{id}/complete")
    public void executeTask (@PathVariable int id){
        services.execute(id);
    }

    @PatchMapping("/{id}/uncomplete")
    public void unexecuteTask (@PathVariable int id){
        services.unexecute(id);
    }

    @DeleteMapping("/{id}")
    public List<Task> deleteTask (@PathVariable int id){
        return services.deleteTask(id);
    }


}
