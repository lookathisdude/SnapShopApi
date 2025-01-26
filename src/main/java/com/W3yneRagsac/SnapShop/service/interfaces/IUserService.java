package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;

import java.util.Optional;

public interface IUserService {
    UserEntity createUser(CreateUserInput createUserInput, String userTimeZone) throws UserFoundException;
    Optional<UserEntity> findUserByName(String name);
    UserEntity updateUser(UpdateUserInput updateUserInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException;
    UserEntity updateEmail(UpdateEmailInput updateEmailInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException;
    UserEntity updatePassword(UpdatePasswordInput updatePasswordInput, Long id, String userTimeZone) throws UserNotFoundException;
    UserEntity deleteUser(DeleteUserInput deleteUserDTO) throws UserNotFoundException;
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Long id);
}
