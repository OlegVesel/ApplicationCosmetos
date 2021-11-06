package com.home.ApplicationCosmetos.service;

import com.home.ApplicationCosmetos.Model.Role;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        return userRepo.findByUsername(s);
    }

    public boolean addUser(User user) {
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateCreate(LocalDate.now());
        if (StringUtils.hasLength(user.getEmail()) && StringUtils.hasText(user.getEmail()))
            user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        if (StringUtils.hasLength(user.getEmail()) && StringUtils.hasText(user.getEmail())) {
            createMail(user);
        }
        return true;
    }

    public void createMail(User user) {
        String message = String.format("Привет, %s! \n" +
                "Для активации своего аккаунта, перейди по ссылке \n " +
                "https://vikina-bag.herokuapp.com/activate/%s", user.getUsername(), user.getActivationCode());
        mailSender.sendMail(user.getEmail(), "Активация аккаунта", message);

    }

    public boolean activateUser(String activationCode) {

        User user = userRepo.findByActivationCode(activationCode);
        if (user == null)
            return false;
        user.setActivationCode(null);
        user.setPassword2(user.getPassword());
        userRepo.save(user);
        return true;
    }
}
