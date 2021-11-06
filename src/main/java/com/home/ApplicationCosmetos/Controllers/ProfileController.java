package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import com.home.ApplicationCosmetos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {
//    @Value("${upload.path}")
//    private String uploadPath;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String showProfile(@AuthenticationPrincipal User user, @PathVariable Long id, Model model){
        model.addAttribute("user", userRepo.findById(id).orElse(null));
        return "Profile";
    }

    @PostMapping("/{id}")
    public String updateUser(@AuthenticationPrincipal User user,
                             @PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) MultipartFile userImageFile,
                             Model model){

        User userFromDB = userRepo.findById(id).orElse(null) ;

        if (!userImageFile.isEmpty()){
            File uploadDir = new File(this.getClass().getClassLoader().getResource("image/userProfileImage").getPath());
            if (!uploadDir.exists())
                uploadDir.mkdir();

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + userImageFile.getOriginalFilename();
            try {
                userImageFile.transferTo(new File(uploadDir + "/" + resultFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            userFromDB.setUserImageFile(resultFileName);
        }

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
            model.addAttribute("editUserError", "Чтобы изменения вступили в силу, перезайдите в приложение");
            userRepo.save(userFromDB);
        }
        model.addAttribute("user", userFromDB);
        return "Profile";
    }
}
