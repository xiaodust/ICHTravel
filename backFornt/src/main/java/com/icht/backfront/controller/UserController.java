package com.icht.backfront.controller;

import com.icht.backfront.dao.UserDAO;
import com.icht.backfront.dataobject.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

@Controller

public class UserController {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RedisTemplate redisTemplate;

   @PostMapping("/user")
   @ResponseBody
    public UserDO save(@RequestBody UserDO userDO) {
       userDAO.save(userDO);
       return userDO;
   }

    @PostMapping("/user/update")
    @ResponseBody
    public UserDO update(@RequestBody UserDO userDO){
        userDAO.update(userDO);
        redisTemplate.opsForValue().set(userDO.getName(),userDO,30, TimeUnit.MINUTES);
        return userDO;
    }

    @PostMapping("/user/del")
    @ResponseBody
    public boolean delete(@RequestParam("id") String id){
        return userDAO.delete(id)>0;
    }

    @PostMapping("/user/findByUserName")
    @ResponseBody
    public UserDO findByUserName(@RequestParam("name") String name){
       return userDAO.findByName(name);
    }

}
