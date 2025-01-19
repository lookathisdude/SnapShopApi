package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.UserEntity;

import java.util.Optional;

public interface IUserService {
    UserEntity createUser(String name, String email, String password) throws UserFoundException;
    UserEntity updateUser(Long id, String name, String email, String password) throws UserFoundException, UserNotFoundException;
    void DeleteUser(Long id) throws UserNotFoundException;
    Optional<UserEntity> findUserByName(String name);
    Optional<UserEntity> findByEmail(String email);
}
