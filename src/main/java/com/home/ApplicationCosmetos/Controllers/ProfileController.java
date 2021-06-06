package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import com.home.ApplicationCosmetos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserRepo userRepo;


    @GetMapping("/{id}")
    public String showProfile(@AuthenticationPrincipal User user,@PathVariable Long id, Model model){
        model.addAttribute("user", userRepo.getById(id));
        return "Profile";
    }

    @PostMapping("/{id}")
    public String updateUser(@AuthenticationPrincipal User user, @Valid User editUser,
                             BindingResult bindingResult, Model model){

        System.out.println(editUser);
        model.addAttribute("user", user);
        return "Profile";
    }
}
