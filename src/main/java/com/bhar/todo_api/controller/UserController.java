import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("api/users")
public class UserController {
    
    private UserService userService;

    public record Credentials(String name, String password){}

    
    public userController(UserSevice userService){
        this.userService = userService;
    }

    @PostMapping ("/register")
    public ResponseEntity<?> register (@RequestBody Credentials creds){
        try{
            User user = userService.register(creds.name(), 
            creds.password());
            return ResponseEntity.status(201)
            .body(Map.of("name", user.getName()));
        }catch (IllegalArgumentExeption e){
            return ResponseEntity.status(409)
            .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping ("/login")
    public login (@RequestBody Credentials creds ){
        if (userService.loggin(creds.name(), creds.password())){
            return ResponseEntity.ok(Map.of("name", creds.name()));
        }
        return ResponseEntity.status(401)
        .body(Map.of("error", "Unvaliable identifiers"));
    }
}
