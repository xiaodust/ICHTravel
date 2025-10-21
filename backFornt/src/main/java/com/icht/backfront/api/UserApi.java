package com.icht.backfront.api;

import com.icht.backfront.model.Result;
import com.icht.backfront.model.User;
import com.icht.backfront.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@Controller
public class UserApi {
    @Autowired
    private UserService userService;

    @PostMapping("/reg")
    @ResponseBody
    public Result<User> register(@RequestParam String name, @RequestParam String password) {
        return userService.register(name, password,password);
    }

    @GetMapping("/login")
    @ResponseBody
    public Result<User> login(@RequestParam String name, @RequestParam String password, HttpServletRequest request) {
        Result<User> result = userService.login(name, password);
        if (result.isSuccess()) {
            request.getSession().setAttribute("userId",result.getData().getId());
        }
        return result;
    }

    @GetMapping("/logout")
    @ResponseBody
    public Result<User> logout(HttpServletRequest request) {
        Result result = new Result();
        request.getSession().removeAttribute("userId");

        result.setSuccess(true);
        return result;
    }

    @PostMapping("/regPwd")
    @ResponseBody
    public Result<User> regPwd(@RequestParam String name, @RequestParam String number, @RequestParam String password) {
    return userService.register(name, number, password);
    }

}
