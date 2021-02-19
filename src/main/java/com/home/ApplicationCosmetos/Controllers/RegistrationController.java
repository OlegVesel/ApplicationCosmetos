package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.Role;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if (userFromDB != null) {
            model.addAttribute("message", "Такой пользователь уже существует");
            return "registration";
        }
        if (user.getUsername().isEmpty() | user.getPassword().isEmpty()){
            model.addAttribute("message", "Поля \"Имя нового пользователя\" и \"Пароль\" должны быть заполнены");
            return "registration";
        } else {
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepo.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user", user);
        return "home";
    }
}
