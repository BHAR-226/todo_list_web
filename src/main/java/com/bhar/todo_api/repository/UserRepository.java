package com.bhar.todo_api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.apache.catalina.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Integer>{
    public boolean existsByName(String name);
    public User findByName (String name);
}
