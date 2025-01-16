package com.infodation.userservice;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.WriteSchemaRequest;
import com.authzed.grpcutil.BearerToken;
import com.google.protobuf.ByteString;
import com.infodation.userservice.services.RoleServiceImpl;
import com.infodation.userservice.services.UserServiceImpl;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@Slf4j
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
