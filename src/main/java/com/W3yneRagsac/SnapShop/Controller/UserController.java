package com.W3yneRagsac.SnapShop.Controller;
import com.W3yneRagsac.SnapShop.DTO.User.CreateUserDTO;
import com.W3yneRagsac.SnapShop.exceptions.EmailNotFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;
import com.W3yneRagsac.SnapShop.service.classes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

//TODO: NEED TO CREATE CONTROLLER FOR USER
@Controller// where the graphql url will be placed
public class UserController {
    @Autowired
    private UserService userService;

    // graphql method for getting the user
    @QueryMapping
    public UserEntity getUserByName(String name) throws UserNotFoundException {
        return userService.findUserByName(name)
                .orElseThrow( () -> new UserNotFoundException("User with name: " + name + " not found."));
    }

    @QueryMapping
    public UserEntity getUserByEmail(String email) throws EmailNotFoundException {
        return userService.findByEmail(email)
                .orElseThrow( () -> new EmailNotFoundException("User with name:" + email + " not found."));
    }

    @MutationMapping
    public UserEntity createUser(@Argument CreateUserDTO userInput) throws UserFoundException {
        try {
            return userService.createUser(userInput.getName(), userInput.getEmail(), userInput.getPassword());
        } catch (UserFoundException e) {
            throw new UserFoundException(
                    String.format("User exists with email: %s and name: %s", userInput.getEmail(), userInput.getName()),
                    e
            );
        }
    }
//    TODO: ADD UPDATE USER AND DELETE USER> TEST THE FUNCTIONS AND CHECK FOR ERRORS
}
