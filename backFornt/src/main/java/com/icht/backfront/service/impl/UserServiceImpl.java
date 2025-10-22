package com.icht.backfront.service.impl;

import com.icht.backfront.dao.UserDAO;
import com.icht.backfront.dataobject.UserDO;
import com.icht.backfront.model.Result;
import com.icht.backfront.model.User;
import com.icht.backfront.service.ShoppingCartService;
import com.icht.backfront.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShoppingCartService shoppingCartService;


    @SuppressWarnings("UnreachableCode")
    @Override
    public Result<User> register(String name, String password,String number) {
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
        if (StringUtils.isEmpty(number)){
            result.setCode("602");
            result.setMessage("手机号不能为空");
            return result;
        }

        UserDO userDO=(UserDO) redisTemplate.opsForValue().get(name);
        if (userDO==null){
           userDO=userDAO.findByName(name);
        }
        if (userDO!=null){
            result.setCode("602");
            result.setMessage("用户名已经存在");
            return  result;
        }
        UserDO userDO1=new UserDO();
        userDO1.setId(UUID.randomUUID().toString());
        userDO1.setName(name);
        userDO1.setPassword(password);
        userDO1.setNumber(number);
        userDO1.setGmtCreated(LocalDateTime.now());
        userDO1.setGmtModified(LocalDateTime.now());
        userDAO.save(userDO1);
        shoppingCartService.insertShoppingCart(userDO1.getId());

        redisTemplate.opsForValue().set(name,userDO1,30,TimeUnit.MINUTES);
        result.setSuccess(true);
        result.setCode("200");
        result.setData(userDO1.ToMode());
        return result;
    }

    @SuppressWarnings("UnreachableCode")
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

       UserDO userDO=(UserDO)redisTemplate.opsForValue().get(name);
        if (userDO==null){
            String emptyFlag = (String)redisTemplate.opsForValue().get("EMPTY_USER_" + name);
            if (emptyFlag != null) {
                userDO = null;
            } else {
                userDO = userDAO.findByName(name);
                if (userDO != null) {
                    redisTemplate.opsForValue().set(name, userDO, 30, TimeUnit.MINUTES);
                } else {
                    redisTemplate.opsForValue().set("EMPTY_USER_" + name, "NOT_FOUND", 5, TimeUnit.MINUTES);
                }
            }
        }
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
    public Result<User> getByUserId(String id) {
        Result<User> result=new Result<>();
        result.setSuccess(true);
        if (StringUtils.isEmpty(id)){
            result.setCode("600");
            result.setMessage("userId不能为空");
            return result;
        }
        UserDO userDO=userDAO.findById(id);
        if (userDO==null){
            result.setCode("601");
            result.setMessage("用户不存在");
            return result;
        }
        result.setData(userDAO.findById(id).ToMode());
        result.setMessage("查询成功");
        return result;
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

    @Override
    public Result<User> forgotPassword(String name, String number,String password) {
        Result<User> result=new Result<>();
        result.setCode("200");
        if (StringUtils.isEmpty(name)){
            result.setCode("600");
            result.setMessage("用户名不能为空");
            return result;
        }
        UserDO userDO=userDAO.findByName(name);
        if (userDO==null){
            result.setCode("601");
            result.setMessage("用户不存在");
            return result;
        }
        userDO.setPassword(password);
        result.setMessage("验证成功");
        userDO.setNumber(number);
        userDAO.update(userDO);
        result.setData(userDO.ToMode());
        return  result;

    }

    @Override
    public Result update(String userId,String nickName, String Signature) {
        Result result=new Result<>();
        result.setSuccess(true);
        result.setMessage("修改成功");
        UserDO userDO=userDAO.findById(userId);
        userDO.setNickName(nickName);
        userDO.setSignature(Signature);
        userDAO.update(userDO);
        return result;
    }


}

