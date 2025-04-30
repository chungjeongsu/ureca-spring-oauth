package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "Hello World";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/user/role")
    public String role(){
        return "role";
    }
}
