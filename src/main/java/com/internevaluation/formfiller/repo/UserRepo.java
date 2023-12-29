package com.internevaluation.formfiller.repo;

import com.internevaluation.formfiller.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity,Integer> {


    UserEntity findByPassword(String password);
    UserEntity findByEmail(String email);

}