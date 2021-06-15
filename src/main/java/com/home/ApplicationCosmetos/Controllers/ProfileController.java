package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import com.home.ApplicationCosmetos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String showProfile(@AuthenticationPrincipal User user, @PathVariable Long id, Model model){
        model.addAttribute("user", userRepo.getById(id));
        return "Profile";
    }

    @PostMapping("/{id}")
    public String updateUser(@AuthenticationPrincipal User user,
                             @PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             Model model){

        User userFromDB = userRepo.getById(id);

        if (userRepo.findByUsername(username) != null && !userFromDB.getUsername().equals(username)){
            model.addAttribute("editUserError", "Пользователь с таким именем уже существует!");
            model.addAttribute("user", user);
            return "Profile";
        }
        else {
            if (StringUtils.hasText(username) && StringUtils.hasLength(username)){
                userFromDB.setUsername(username);
            }
            if (StringUtils.hasText(email) && StringUtils.hasLength(email) &&
                    !email.equals("Вы не указали Email при регистрации") &&
                    !(userFromDB.getEmail() != null ? userFromDB.getEmail().equals(email) : false )){

                userFromDB.setEmail(email);
                userFromDB.setActivationCode(UUID.randomUUID().toString());
                userService.createMail(userFromDB);

            }
            userFromDB.setPassword2(userFromDB.getPassword());
            model.addAttribute("editUserError", "Чтобы измененеия вступили в силу, перезайдите в приложение");
            userRepo.save(userFromDB);
        }
        model.addAttribute("user", userFromDB);
        return "Profile";
    }
}
