import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service 
public class UserService {

    private UserRepository uRepo;
    private  final PasswordEncoder encoder = new BcryptPasswordEncoder();

    public UserService (UserRepository uRepo){
        this.uRepo= uRepo;
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
        return encoder.matches(rawPassword, user.getPwd_hash());
    }
    
    public void logout(){}
}
