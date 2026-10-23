package com.bhar.todo_api.config;

import com.bhar.todo_api.models.Task;
import com.bhar.todo_api.models.User;
import com.bhar.todo_api.repository.TaskRepository;
import com.bhar.todo_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder encoder;
    private final String adminPassword;

    public AdminBootstrap(UserRepository userRepository,
                          TaskRepository taskRepository,
                          PasswordEncoder encoder,
                          @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        User admin = userRepository.findByName("admin");
        if (admin == null) {
            admin = new User();
            admin.setName("admin");
            admin.setPw_hash(encoder.encode(adminPassword));
            admin = userRepository.save(admin);
        }

        List<Task> orphans = taskRepository.findByUserIsNull();
        if (!orphans.isEmpty()) {
            for (Task task : orphans) {
                task.setUser(admin);
            }
            taskRepository.saveAll(orphans);
        }
    }
}
