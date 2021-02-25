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

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException{

            return userRepo.findByUsername(s);
    }

    public boolean addUser(User user){
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if(userFromDB!=null) {
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        return true;
    }
}
