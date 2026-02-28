package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.NewUserDto;
import com.juliana.api_juliana.entities.Role;
import com.juliana.api_juliana.entities.User;
import com.juliana.api_juliana.enums.RoleList;
import com.juliana.api_juliana.jwt.JwtUtil;
import com.juliana.api_juliana.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public String authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        return jwtUtil.generateToken(authResult);
    }

    public void registerNormalUser(NewUserDto dto){
        registerUser(dto, RoleList.ROLE_USER);
    }

    public void registerAdmin(NewUserDto dto){
        registerUser(dto, RoleList.ROLE_ADMIN);
    }

    public void registerUser(NewUserDto newUserDto, RoleList roleList){

        if (userService.existsByUsername(newUserDto.getUsername())){
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Role role = roleRepository.findByName(roleList)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User(
                newUserDto.getUsername(),
                passwordEncoder.encode(newUserDto.getPassword()),
                newUserDto.getEmail(),
                role
        );

        userService.save(user);
    }
}
