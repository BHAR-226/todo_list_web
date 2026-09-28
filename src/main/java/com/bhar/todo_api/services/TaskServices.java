package com.bhar.todo_api.services;

import com.bhar.todo_api.models.Task;
import org.springframework.stereotype.Service;
import com.bhar.todo_api.repository.TaskRepository;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static com.bhar.todo_api.models.Statut.Finished;
import static com.bhar.todo_api.models.Statut.Waiting;

@Service
public class TaskServices {
    private TaskRepository repo;

    public TaskServices(TaskRepository repo){
        this.repo = repo;
    }

    public Task newTask (String name, String description,
                         LocalDate deadline, Integer priority){
        Task task = new Task();

        task.setName(name);
        if (description!= null && !description.isBlank() ){ task.setDescription(description);}
        if (deadline!= null) {task.setDeadline (deadline);}
        if (priority != null){ task.setPriority(priority);}

        repo.save(task);
        return task;
    }

    public Task modifyTask (int id, String name, String description,
                            LocalDate deadline, Integer priority){
        Task taskToModify = repo.findById(id).orElse(null);
        if (taskToModify == null){return null;}//handle the case when it return a null value
        if(name!=null && !name.isBlank()) {taskToModify.setName(name);}
        if (description!= null && !description.isBlank() ){ taskToModify.setDescription(description);}
        if (deadline!= null) {taskToModify.setDeadline (deadline);}
        if (priority != null){ taskToModify.setPriority(priority);}
        return repo.save(taskToModify);
    }

    public List<Task> taskList (){
        List <Task> tasks = repo.findAll();
        // return a sorted task list sorted first by statut,
        // then deadline and finaly priority
        tasks.sort( Comparator.comparing ((Task t)->t.getStatut())
                .thenComparing(Task::getDeadline,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getPriority, Comparator.reverseOrder())
        );
        return tasks;
    }

    public List<Task> search(String searchInput){
        List<Task> results= new ArrayList<Task>();
        // the user can search a task by writing the name or the id
        try{
            int id = parseInt(searchInput);
            Task task = repo.findById(id).orElse(null);
            if (task != null) {
                results.add(task);
            }
        } catch (NumberFormatException e){
            results = repo.findByName(searchInput);
        }
        return results;
    }
    public Task getTask (int id ){
        return repo.findById(id).orElse(null);}

    public List<Task> filter(String filterInput){
        // the user can filtr the task by priority, deadline,
        List<Task> results= new ArrayList<Task>();

        try {
            int priority = parseInt(filterInput);
            results =repo.findByPriority(priority);
        } catch (NumberFormatException e){
            try{
                LocalDate deadline = LocalDate.parse(filterInput);
                results = repo.findAll().stream()
                        .filter(t->t.getDeadline() !=null && t.getDeadline().equals(deadline))
                        .collect(Collectors.toList());
            }catch (DateTimeParseException ex ){
                System.out.println ("Invalid filter");
            }
        }
        return results;
    }

    public void execute(int id){
        Task task = repo.findById(id).orElse(null);
        if (task ==null){
            return;
        }
        task.setStatut(Finished);
        repo.save(task);
    }

    public void unexecute(int id){
        Task task = repo.findById(id).orElse(null);
        if (task ==null){return ;}
        task.setStatut(Waiting);
        repo.save(task);
    }

    public List<Task> getFinishedTask(){
        //to print the finished task
        return repo.findByStatut(Finished);
    }

    public void deleteTask(int id){
        repo.deleteById(id);
    }

}
