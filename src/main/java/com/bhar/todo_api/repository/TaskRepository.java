package com.bhar.todo_api.repository;

import com.bhar.todo_api.models.Statut;
import com.bhar.todo_api.models.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {
    private List<Task> tasks;

    public TaskRepository(List<Task> tasks) {
        this.tasks = tasks;
    }
    public TaskRepository() {
        this.tasks = new ArrayList<>();
    }

   public void createTask (Task task){
        this.tasks.add(task);
   }

   public Task getTaskById (int id){
        for (Task t : tasks) {
            if (t.getId() == id) return t;
        }
        return null;
    }
   public List<Task> getTasksByName(String name){
       return tasks.stream()
               .filter(t -> t.getName().toLowerCase()
                       .contains(name.toLowerCase()))
               .collect(Collectors.toList());
    }
   public List<Task> getTaskByStatut (Statut statut){
        return tasks.stream()
                .filter(t->t.getStatut().equals(statut))
                .collect(Collectors.toList());
    }
   public List<Task> getTaskByPriority (int priority){
        return tasks.stream()
                .filter(t->t.getPriority() == priority)
                .collect(Collectors.toList());
    }
   public List<Task> getAllTask (){
        return tasks;
    }

   public List<Task> deleteTaskById (int id){
        Task taskRemove = tasks.stream()
                .filter(t->t.getId()==id)
                .findFirst().orElse(null);
        if (taskRemove != null){
            tasks.remove(taskRemove);}
        return tasks;
    }

   public void modifyTaskBy (Task taskToModify, Task newTask){
       taskToModify.setName(newTask.getName());
       taskToModify.setDescription(newTask.getDescription());
       taskToModify.setDeadline(newTask.getDeadline());
       taskToModify.setPriority(newTask.getPriority());
       taskToModify.setStatut(newTask.getStatut());

       //
   }
}
