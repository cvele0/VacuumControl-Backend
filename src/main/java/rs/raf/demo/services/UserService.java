package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, IService<User, Long> {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public <S extends User> S save(S user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long  userID) {
        return userRepository.findById(userID);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long userID) {
        userRepository.deleteById(userID);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByEmail(email);
        if (myUser == null) {
            throw new UsernameNotFoundException("User name " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(myUser.getEmail(), myUser.getHashedPassword(), new ArrayList<>());
    }

    public int getPermissionsByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            return user.getPermissions();
        } else {
            // Handle scenario where user with given email doesn't exist
            return 0; // Or any default value for permissions
        }
    }
}
