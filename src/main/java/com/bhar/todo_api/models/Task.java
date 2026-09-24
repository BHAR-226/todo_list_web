package com.bhar.todo_api.models;

import java.time.LocalDate;

import static com.bhar.todo_api.models.Statut.Waiting;

public class Task {
    private int id;
    private String name;
    String description;
    LocalDate deadline;
    Statut  statut = Waiting;
    int priority =1;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getPriority() {
        return priority;
    }

    public int getId() {
        return id;
    }

}
