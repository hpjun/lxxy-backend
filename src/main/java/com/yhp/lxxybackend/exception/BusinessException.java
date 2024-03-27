package com.yhp.lxxybackend.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String msg) {
        super(msg);
    }
}
