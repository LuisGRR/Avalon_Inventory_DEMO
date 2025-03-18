package com.avalon.Avalon_Inventory.domain.repository;

import com.avalon.Avalon_Inventory.domain.model.User;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    User findByUsernameOrId(String username, Long id);
}
