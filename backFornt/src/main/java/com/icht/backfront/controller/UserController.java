package com.icht.backfront.controller;

import com.icht.backfront.dao.UserDAO;
import com.icht.backfront.dataobject.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserDAO userDAO;

    @PostMapping("/user")
    @ResponseBody
    public UserDO save(@RequestBody UserDO userDO) {
        userDAO.save(userDO);
        return userDO;
    }

    @PutMapping("/user/{id}")
    @ResponseBody
    public UserDO update(@RequestBody UserDO userDO){
        userDAO.update(userDO);
        return userDO;
    }

    @DeleteMapping("/user/{id}}")
    @ResponseBody
    public boolean delete(@PathVariable("id") String id){
        return userDAO.delete(id)>0;
    }

    @GetMapping("/user")
    @ResponseBody
    public UserDO findByUserName(@RequestParam("name") String name){
        return userDAO.findByName(name);
    }

}
