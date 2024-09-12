package com.example.shopapp.services.impls;

import com.example.shopapp.models.Role;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
