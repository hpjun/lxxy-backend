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

    private Long minTime;        //最小时间戳，指定查询范围，固定范围后就可以固定下标了，第一次由前端查询当前时间
    private Integer offset;        //偏移量，给前端下次带上，便于跳过多少个元素，第一次spring指定0


    public static <T> Result<T> ok(){
        return new Result<T>(true, null, null, null, null, null);
    }
    public static <T> Result<T> ok(T data){
        return new Result<T>(true, null, data, null, null, null);
    }
    public static <T> Result<T> ok(T data, Long total){
        return new Result<T>(true, null, data, total, null, null);
    }
    public static <T> Result<T> ok(T data, Long minTime, Integer offset){
        return new Result<T>(true, null, data, null, minTime, offset);
    }
    public static <T> Result<T> fail(String errorMsg){
        return new Result<T>(false, errorMsg, null, null, null, null);
    }
}