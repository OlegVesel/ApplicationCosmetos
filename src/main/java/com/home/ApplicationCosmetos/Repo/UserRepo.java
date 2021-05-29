package com.home.ApplicationCosmetos.Repo;

import com.home.ApplicationCosmetos.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long>{
    User findByUsername(String username);

    User findByActivationCode(String activationCode);

}
