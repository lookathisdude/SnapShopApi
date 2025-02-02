package com.W3yneRagsac.SnapShop.service.interfaces;

import com.W3yneRagsac.SnapShop.DTO.User.*;
import com.W3yneRagsac.SnapShop.exceptions.UserFoundException;
import com.W3yneRagsac.SnapShop.exceptions.UserNotFoundException;
import com.W3yneRagsac.SnapShop.model.Entity.UserEntity;

import java.util.List;

public interface IUserService {
    UserEntity createUser(CreateUserInput createUserInput, String userTimeZone) throws UserFoundException;
    List<UserEntity> getUserByFilter(SearchUsersInput searchUsersInput);
    UserEntity getUserById(GetUserByIdInput getUserByIdInput) throws UserNotFoundException;
    UserEntity updateUserCredentials(UpdateUserCredentialsInput updateUserCredentialsInput, Long id, String userTimeZone) throws UserNotFoundException, UserFoundException;
    UserEntity deleteUser(DeleteUserInput deleteUserDTO) throws UserNotFoundException;
}
