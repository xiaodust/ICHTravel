package com.icht.backfront.service;

import com.icht.backfront.model.Result;
import com.icht.backfront.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserService {
   public Result<User> register(String name, String password,String number);

   public Result<User> login(String name, String password);

   public Result<User> getByUserId(String id);

   public Boolean checkLogin(HttpServletRequest request);

   public Result<User> forgotPassword(String name,String number,String password);

   public Result<User> update(String userId,String nickName,String Signature);

   public Result<User> updateAvatar(String userId,String avatar);
}
