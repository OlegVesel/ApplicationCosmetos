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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(){
        return "Login";
    }


    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        if (user.getActivationCode() != null)
            model.addAttribute("messageActivation", "У вас не активирован аккаунт, для активации "+
                    "аккаунта пройдите по ссылке в письме в вашем почтовом ящике");
        return "Home";
    }

    @GetMapping("/registration")
    public String registration() {
        return "Registration";
    }

    @GetMapping("/activate/{activationCode}")
    public String activate(@PathVariable String activationCode, Model model){

        boolean isActivate = userService.activateUser(activationCode);
        if (isActivate)
            model.addAttribute("messageActivation", "Активация прошла успешно");
        else model.addAttribute("messageActivation", "Код активации не найден");

        return "Login";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {

        if (!StringUtils.isEmpty(user.getPassword()) && !user.getPassword().equals(user.getPassword2())) {
            model.addAttribute("passwordError", "Пароли не совпадают!");
            model.addAttribute("newUser", user);
            return "Registration";
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("newUser", user);
            return "Registration";

        }

        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "Такой пользователь уже существует");
            model.addAttribute("newUser", user);
            return "Registration";
        }

        return "redirect:/login";
    }

}
