package com.example.redistest.service;

import com.example.redistest.model.Role;
import com.example.redistest.model.User;
import com.example.redistest.repository.RoleRepository;
import com.example.redistest.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public void addRoleToUser(User user, String role) {
        if (!user.hasRole(role)){
            Role newRole = Role.builder().name(role).build();
            roleRepository.save(newRole);
            user.addRole(newRole);
            userRepository.save(user);
        }


    }

    public void removeRoleFromUser(User user, String role) {
        Long id = user.getRoleId(role);
        if (id != -1L){
            user.removeRole(id);
            roleRepository.deleteById(id);
            userRepository.save(user);
        }
    }

}
