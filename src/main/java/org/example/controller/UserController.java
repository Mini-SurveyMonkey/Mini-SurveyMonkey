package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/login")
//    public String login(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return "login";
//        }
//        return "redirect:/";
//    }
}
