package com.bhar.todo_api.services;

import com.bhar.todo_api.models.Task;
import com.bhar.todo_api.models.User;
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

    public Task newTask (User user, String name, String description,
                         LocalDate deadline, Integer priority){
        Task task = new Task();

        task.setUser(user);
        task.setName(name);
        if (description!= null && !description.isBlank() ){ task.setDescription(description);}
        if (deadline!= null) {task.setDeadline (deadline);}
        if (priority != null){ task.setPriority(priority);}

        repo.save(task);
        return task;
    }

    public Task modifyTask (User user, int id, String name, String description,
                            LocalDate deadline, Integer priority){
        Task taskToModify = ownedTask(user, id);
        if (taskToModify == null){return null;}
        return applyModification(taskToModify, name, description, deadline, priority);
    }

    private Task applyModification (Task taskToModify, String name, String description,
                                    LocalDate deadline, Integer priority){
        if(name!=null && !name.isBlank()) {taskToModify.setName(name);}
        if (description!= null && !description.isBlank() ){ taskToModify.setDescription(description);}
        if (deadline!= null) {taskToModify.setDeadline (deadline);}
        if (priority != null){ taskToModify.setPriority(priority);}
        return repo.save(taskToModify);
    }

    // ---- legacy overloads used only by the console view (TaskView), to remove with it ----

    @Deprecated
    public Task newTask (String name, String description,
                         LocalDate deadline, Integer priority){
        return newTask(null, name, description, deadline, priority);
    }

    @Deprecated
    public Task modifyTask (int id, String name, String description,
                            LocalDate deadline, Integer priority){
        Task taskToModify = repo.findById(id).orElse(null);
        if (taskToModify == null){return null;}
        return applyModification(taskToModify, name, description, deadline, priority);
    }

    @Deprecated
    public Task getTask (int id){
        return repo.findById(id).orElse(null);
    }

    @Deprecated
    public void execute (int id){
        Task task = repo.findById(id).orElse(null);
        if (task == null){return;}
        task.setStatut(Finished);
        repo.save(task);
    }

    public List<Task> taskList (User user){
        return sortTasks(repo.findByUser(user));
    }

    // used by the legacy console view (TaskView) - to remove with it
    public List<Task> taskList (){
        return sortTasks(repo.findAll());
    }

    private List<Task> sortTasks (List<Task> tasks){
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
    public Task getTask (User user, int id ){
        return ownedTask(user, id);
    }

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

    public boolean execute(User user, int id){
        Task task = ownedTask(user, id);
        if (task ==null){
            return false;
        }
        task.setStatut(Finished);
        repo.save(task);
        return true;
    }

    public boolean unexecute(User user, int id){
        Task task = ownedTask(user, id);
        if (task ==null){return false;}
        task.setStatut(Waiting);
        repo.save(task);
        return true;
    }

    public List<Task> getFinishedTask(){
        //to print the finished task
        return repo.findByStatut(Finished);
    }

    public boolean deleteTask(User user, int id){
        Task task = ownedTask(user, id);
        if (task == null){return false;}
        repo.delete(task);
        return true;
    }

    // returns the task only if it exists and belongs to this user
    private Task ownedTask(User user, int id){
        Task task = repo.findById(id).orElse(null);
        if (task == null || user == null) {return null;}
        if (task.getUser() == null) {return null;}
        if (task.getUser().getId() != user.getId()) {return null;}
        return task;
    }

}
