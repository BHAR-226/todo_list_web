package com.bhar.todo_api.services;

import com.bhar.todo_api.models.User;
import com.bhar.todo_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository uRepo;
    private final PasswordEncoder encoder;

    public UserService (UserRepository uRepo, PasswordEncoder encoder){
        this.uRepo= uRepo;
        this.encoder = encoder;
    }
    public User register ( String name, String rawPassword){
        if (uRepo.existsByName(name)){
            throw new IllegalArgumentException ("Name already taken");
        }
        User user = new User();
        user.setName(name);
        user.setPw_hash(encoder.encode(rawPassword));
        return uRepo.save(user);
    }

    public boolean login(String name, String rawPassword){
        User user = uRepo.findByName(name);
        if (user == null) return false;
        return encoder.matches(rawPassword, user.getPw_hash());
    }

    public User findByName(String name){
        return uRepo.findByName(name);
    }

    public void logout(){}
}
