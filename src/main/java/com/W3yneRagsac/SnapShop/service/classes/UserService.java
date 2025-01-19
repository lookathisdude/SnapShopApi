package com.W3yneRagsac.SnapShop.service.classes;

import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;

import com.W3yneRagsac.SnapShop.repository.UserRepository;
import com.W3yneRagsac.SnapShop.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserEntity createUser(String name, String email, String password) throws UserFoundException {
        // if the username is already taken and email taken
        if(userRepository.findByEmail(email) !=null) {
            throw new UserFoundException("The email is taken with the name of: " + email);
        }
        if(userRepository.findUserByName(name) != null) {
            throw new UserFoundException("The user is taken with the name of:" + name);
        }
        // if it passes the checks, create the user. If the if statement is true, it will show the error
        UserEntity user = new UserEntity();  // define the user
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);

        // save the user
        return userRepository.save(user);
        }

    @Override
    public UserEntity updateUser(Long id, String name, String email, String password) throws UserNotFoundException, UserFoundException {
        // Check if the user exists
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if the email or username is already taken by another user
        if ((userRepository.findByEmail(email) != null && !existingUser.getEmail().equals(email)) ||
                (userRepository.findUserByName(name) != null && !existingUser.getName().equals(name))) {
            throw new UserFoundException("The username or email is already taken: " + name + ", " + email);
        }

        // If passed the checks, update the user
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setPassword(password);  // Consider hashing the password before saving

        // Save the updated user
        return userRepository.save(existingUser);
    }


    @Override
    public void DeleteUser(Long id) throws UserNotFoundException{
        // first check if user exists
        UserEntity isExisting = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id" + id));

        // if the user exists, delete it
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserEntity> findUserByName(String name) {
        return Optional.ofNullable(userRepository.findUserByName(name));
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }
}
