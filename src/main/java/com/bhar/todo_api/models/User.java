package com.bhar.todo_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity 
@Table(name = "app_user")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (unique = true, nullable = false)
    private String name;

    @JsonIgnore 
    private String pw_hash ;

    public String getName (){return this.name;}
    public void setName (String name) {this.name = name;}

    public int getId () {return this.id;}
    
    public String getPw_hash() { return pw_hash; }
    public void setPw_hash(String pw_hash) 
        { this.pw_hash = pw_hash; }

}
