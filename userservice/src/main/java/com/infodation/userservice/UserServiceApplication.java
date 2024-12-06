package com.infodation.userservice;

import com.infodation.userservice.services.RoleServiceImpl;
import com.infodation.userservice.services.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserServiceApplication implements CommandLineRunner{

	private final UserServiceImpl userService;
	private final RoleServiceImpl roleService;

    public UserServiceApplication(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		roleService.createDefaultRoles();
		userService.createDefaultUsers();
	}

}
