package com.example.userservice.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Hidden
    @GetMapping("/")
    public String redirectToUsers() {
        return "redirect:/users";
    }
}
