package com.bhar.todo_api.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.bhar.todo_api.models.Statut.Waiting;
import jakarta.persistence.*;
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    LocalDate deadline;
    private Statut  statut = Waiting;
    private Integer priority =1;

    @ManyToOne (fetch = FetchType.LAZY)
    @JsonIgnore 
    private User user ;

    public User getUser(){return this.user;}
    public void setUser(User user){this.user = user;}

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
