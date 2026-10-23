package com.bhar.todo_api.controller;

import com.bhar.todo_api.models.User;
import com.bhar.todo_api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/users")
public class UserController {

    private UserService userService;

    private final SecurityContextRepository contextRepository =
            new HttpSessionSecurityContextRepository();

    public record Credentials(String name, String password){}


    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping ("/register")
    public ResponseEntity<?> register (@RequestBody Credentials creds,
                                       HttpServletRequest request,
                                       HttpServletResponse response){
        try{
            User user = userService.register(creds.name(),
            creds.password());
            openSession(user.getName(), request, response);
            return ResponseEntity.status(201)
            .body(Map.of("name", user.getName()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(409)
            .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping ("/login")
    public ResponseEntity<?> login (@RequestBody Credentials creds,
                                    HttpServletRequest request,
                                    HttpServletResponse response){
        if (userService.login(creds.name(), creds.password())){
            openSession(creds.name(), request, response);
            return ResponseEntity.ok(Map.of("name", creds.name()));
        }
        return ResponseEntity.status(401)
        .body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping ("/me")
    public ResponseEntity<?> me (Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of("name", authentication.getName()));
    }

    private void openSession(String name, HttpServletRequest request,
                             HttpServletResponse response){
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                name, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
    }
}
