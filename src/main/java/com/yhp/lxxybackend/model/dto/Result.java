package com.yhp.lxxybackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private Boolean success;
    private String errorMsg;
    private T data;
    private Long total;

    public static <T> Result<T> ok(){
        return new Result<T>(true, null, null, null);
    }
    public static <T> Result<T> ok(T data){
        return new Result<T>(true, null, data, null);
    }
    public static <T> Result<T> ok(T data, Long total){
        return new Result<T>(true, null, data, total);
    }
    public static <T> Result<T> fail(String errorMsg){
        return new Result<T>(false, errorMsg, null, null);
    }
}