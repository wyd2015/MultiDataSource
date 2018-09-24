package com.ethan.datasource.controller;

import com.ethan.datasource.model.po.User;
import com.ethan.datasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public String insertUser(String name, String password){
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        userService.insertUser(user);
        return "sucess";
    }

    @GetMapping
    public Map<String, Object> getUserList(){
        Map<String, Object> map = new HashMap<String, Object>();
        List<User> userList = userService.getUserList();
        map.put("list", userList);
        return map;
    }

}
