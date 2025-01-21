package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;

import java.util.Optional;

public interface IUserService {
    UserEntity createUser(CreateUserDTO createUserDTO, String userTimeZone) throws UserFoundException;
    Optional<UserEntity> findUserByName(String name);
    UserEntity updateUser(UpdateUserDTO updateUserDTO, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException;
    UserEntity updateEmail(UpdateEmailDTO updateEmailDTO, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException;
    UserEntity updatePassword(UpdatePasswordDTO updatePasswordDTO, Long id, String userTimeZone) throws UserNotFoundException;
    UserEntity deleteUser(DeleteUserDTO deleteUserDTO) throws UserNotFoundException;
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Long id);
}
