package com.icht.backfront.service;

import com.icht.backfront.model.Result;
import com.icht.backfront.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
   public Result<User> register(String name, String password,String number);

   public Result<User> login(String name, String password);

   public Boolean checkLogin(HttpServletRequest request);

   public Result<User> forgotPassword(String name,String number,String password);

}
