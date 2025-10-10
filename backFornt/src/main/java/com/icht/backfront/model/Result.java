package com.icht.backfront.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Result<D> implements Serializable {
@JsonProperty("isSuccess")
    private boolean isSuccess = false;
    private String code;
    private String message;
    private D data;
    public static <T> Result<T> create() {
        return new Result<T>();
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
