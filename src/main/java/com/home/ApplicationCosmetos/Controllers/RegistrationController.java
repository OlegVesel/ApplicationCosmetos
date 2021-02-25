package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {

        if (!StringUtils.isEmpty(user.getPassword()) && !user.getPassword().equals(user.getPassword2())) {
            model.addAttribute("passwordError", "Пароли не совпадают!");
            model.addAttribute("newUser", user);
            return "registration";
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("newUser", user);
            return "registration";

        }

        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "Такой пользователь уже существует");
            model.addAttribute("newUser", user);
            return "registration";
        }

        return "redirect:/login";

    }

}
