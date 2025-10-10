package com.icht.backfront.service.impl;

import com.icht.backfront.dao.UserDAO;
import com.icht.backfront.dataobject.UserDO;
import com.icht.backfront.model.Result;
import com.icht.backfront.model.User;
import com.icht.backfront.service.UserService;
import org.apache.commons.lang3.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Override
    public Result<User> register(String name, String password) {
        Result<User> result=new Result<>();
        if (StringUtils.isEmpty(name)){
            result.setCode("600");
            result.setMessage("用户名不能为空");
            return result;
        }
        if (StringUtils.isEmpty(password)){
            result.setCode("601");
            result.setMessage("密码不能为空");
            return result;
        }
        UserDO userDO=new UserDO();
        userDO=userDAO.findByName(name);

        if (userDO!=null){
            result.setCode("602");
            result.setMessage("用户名已经存在");
            return  result;
        }
        UserDO userDO1=new UserDO();
        userDO1.setId(UUID.randomUUID().toString());
        userDO1.setName(name);
        userDO1.setPassword(password);
        userDAO.save(userDO1);

        result.setSuccess(true);
        result.setCode("200");
        result.setData(userDO1.ToMode());
        return result;
    }

    @Override
    public Result<User> login(String name, String password) {
        Result<User> result=new Result<>();

        if (StringUtils.isEmpty(name)){
            result.setCode("600");
            result.setMessage("用户名不能为空");
            return result;
        }
        if (StringUtils.isEmpty(password)){
            result.setCode("601");
            result.setMessage("密码不能为空");
            return result;
        }

        UserDO userDO=userDAO.findByName(name);

        if (userDO==null){
            result.setCode("602");
            result.setMessage("用户名不存在");
            return  result;
        }

        if (!StringUtils.equals(password,userDO.getPassword())){
            result.setCode("603");
            result.setMessage("密码错误");
            return result;
        }

        result.setSuccess(true);
        result.setCode("200");
        result.setData(userDO.ToMode());
        return  result;
    }

    @Override
    public Boolean checkLogin(HttpServletRequest request) {
        Object userId=request.getSession().getAttribute("userId");
        if (userId==null){
            return false;
        }
        else {
            return true;
        }
    }

}
