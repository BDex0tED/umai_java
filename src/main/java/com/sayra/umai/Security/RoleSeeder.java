package com.sayra.umai.Security;

import com.sayra.umai.UserPackage.Entities.Role;
import com.sayra.umai.UserPackage.Entities.UserEntity;
import com.sayra.umai.UserPackage.Repo.RoleRepo;
import com.sayra.umai.UserPackage.Repo.UserEntityRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RoleSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepo userRepo;
    private final RoleRepo roleRepo;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "krytayakartohaloha1000-7";
    public RoleSeeder(RoleRepo roleRepo,
                      UserEntityRepo userRepo,
                      PasswordEncoder passwordEncoder){
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var role_user = roleRepo.findByName("ROLE_USER").orElseGet(() -> {
            var r = new Role();
            r.setName("ROLE_USER");
            return roleRepo.save(r);
        });
        var role_admin = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> {
            var r = new Role();
            r.setName("ROLE_ADMIN");
            return roleRepo.save(r);
        });
        boolean isAdmin = userRepo.existsByRoles_Name("ROLE_ADMIN");
        if (!isAdmin) {
            if (!userRepo.existsByUsername(ADMIN_USERNAME)) {
                var user = new UserEntity();
                user.setUsername(ADMIN_USERNAME);
                user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                user.setRoles(new java.util.ArrayList<>(java.util.Collections.singletonList(role_admin)));
                userRepo.save(user);
            } else {
                var user = userRepo.findByUsername(ADMIN_USERNAME).orElseThrow();
                if (user.getRoles().stream().noneMatch(r -> "ROLE_ADMIN".equals(r.getName()))) {
                    var roles = new java.util.ArrayList<>(user.getRoles());
                    roles.add(role_admin);
                    user.setRoles(roles);
                    userRepo.save(user);
                }
            }
        }
    }
}
