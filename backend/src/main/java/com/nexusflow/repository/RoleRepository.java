package com.nexusflow.repository;

import com.nexusflow.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

    List<Role> findAllByOrderByRoleNameAsc();
}