package com.W3yneRagsac.SnapShop.exceptions;

public class UserFoundException extends Exception{
    public UserFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
    public UserFoundException(String errorMessage) {
        super(errorMessage);
        }
    }
