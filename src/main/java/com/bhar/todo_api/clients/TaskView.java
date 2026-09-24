package com.bhar.todo_api.clients;

import com.bhar.todo_api.models.Task;
import com.bhar.todo_api.services.TaskServices;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TaskView {
    private TaskServices services;
    private Scanner scan ;

    public TaskView(TaskServices services, Scanner scan) {
        this.services = services;
        this.scan = scan;
    }
    public void printTask(int id){
        //Print a task with his details
        Task task = services.getTask(id);
        System.out.println("ID: "+ task.getId()+"\n"+
                "Name: "+ task.getName()+"\n"+
                "Description: "+ task.getDescription()+"\n"+
                "Deadline: "+ task.getDeadline()+"\n"+
                "Statut: "+ task.getStatut()+"\n"+
                "Priority: "+ task.getPriority()+"\n");
    }
    public void printTask(Task task){
        System.out.println("ID: "+ task.getId()+"\n"+
                "Name: "+ task.getName()+"\n"+
                "Description: "+ task.getDescription()+"\n"+
                "Deadline: "+ task.getDeadline()+"\n"+
                "Statut: "+ task.getStatut()+"\n"+
                "Priority: "+ task.getPriority()+"\n");
    }

    public void  newTask (){
        System.out.println("Name : " );
        String name = scan.nextLine();

        System.out.println("description : " );
        String description = scan.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int loop = 1;
        LocalDate deadline = null;
        while (loop ==1) {
            loop = 2;
            System.out.println("Deadline : ");
            String deadlineInput = scan.nextLine();
            if (!deadlineInput.isBlank()) {
                try {
                    deadline = LocalDate.parse(deadlineInput, formatter);
                } catch (DateTimeParseException e) {
                    loop = 1;
                    System.out.println("Please, write a correct date !");
                }
            }
        }
            Integer priority = null;
            System.out.println(" Priority: ");
            String priorityInput = scan.nextLine();
            if (!priorityInput.isBlank()) {
                priority = Integer.parseInt(priorityInput);
                //add a try catch to handel the case the user write something not a number
            }

            Task task = services.newTask(name, description,
                    deadline, priority);
            System.out.println("New task created : ");
            printTask(task);


    }

    public void  modify(int id){

        System.out.println("Name : " );
        String name = scan.nextLine();

        System.out.println("description : " );
        String description = scan.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int loop = 1;
        LocalDate deadline = null;
        while (loop ==1) {
            loop = 2;
            System.out.println("Deadline : ");
            String deadlineInput = scan.nextLine();
            if (!deadlineInput.isBlank()) {
                try {
                    deadline = LocalDate.parse(deadlineInput, formatter);
                } catch (DateTimeParseException e) {
                    loop = 1;
                    System.out.println("Please, write a correct date !");
                }
            }
        }
                Integer priority = null;
                System.out.println(" Priority: ");
                String priorityInput = scan.nextLine();
                if (!priorityInput.isBlank()) {
                    priority = Integer.parseInt(priorityInput);
                } //add a try catch to handel the case the user write something not a number


        Task taskModify = services.modifyTask( id,name, description,
                deadline, priority);
            System.out.println("Task modified : ");
            printTask(taskModify);
    }


    public void showTasks(){
        List<Task> tasks = services.taskList();
        tasks.stream().forEach(t->
                System.out.println("Name : "+t.getName()+"\n"+
                        "Deadline : "+ t.getDeadline()));
    }
    public void showTasksInDetails(){
        List<Task> tasks = services.taskList();
        tasks.stream().forEach(this::printTask);
    }
    public void showTasks(List<Task> tasks){
        tasks.stream().forEach(t->
                System.out.println("Name : "+t.getName()+"\n"+
                        "Deadline : "+ t.getDeadline()));
    }
    public void showTasksInDetails(List<Task> tasks){
        tasks.stream().forEach(this::printTask);
    }

    public void searchTasks(){
        System.out.println("Write the name or the id : ");
        String search_input = scan.nextLine();
        List<Task> results = services.search(search_input);
        showTasks(results);
    }

    public void filterTask (){
        System.out.println ("Filtre the task by deadline or priority. \n Enter the deadline or the priority :");
        String filterInput = scan.nextLine();
        List<Task> tasks = services.filter(filterInput);
        showTasksInDetails(tasks);
    }
    public void executeTask(int id){
        services.execute(id);
        showTasks();
    }
    public void showFinishedTask (){
        showTasks (services.getFinishedTask());
    }
    public void deleteTask(int id){
        System.out.println("The task is deleted!");
        showTasks(services.deleteTask(id));
    }
}
