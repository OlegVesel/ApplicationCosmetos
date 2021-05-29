package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.Role;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.CosmeticProductRepo;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CosmeticProductRepo cosmeticProductRepo;

    @GetMapping
    public String userList(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("users", userRepo.findAll());
        return "UserList";
    }

    @GetMapping("{userEdit}")
    public String userEditForm(@AuthenticationPrincipal User user, @PathVariable User userEdit, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("userEdit", userEdit);
        model.addAttribute("roles", Role.values());


        return "UserEdit";
    }

    @PostMapping
    public String editUser(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User userEdit) {

        userEdit.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        userEdit.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                userEdit.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(userEdit);
        return "redirect:/user";
    }

    @PostMapping("{id}")
    public String deleteUser(@AuthenticationPrincipal User user, @PathVariable Long id, Model model){

        User deletedUser = userRepo.findById(id).orElse(null);
        model.addAttribute("user",user);
        model.addAttribute("users", userRepo.findAll());
        if (deletedUser!=null)
            model.addAttribute("message", "Пользователь "+ deletedUser.getUsername() + " успешно удален!");
        else model.addAttribute("message", "О пользователе нет записей в БД, не получилось удалить");
        userRepo.deleteById(id);

        return "redirect:/user";

    }
}
