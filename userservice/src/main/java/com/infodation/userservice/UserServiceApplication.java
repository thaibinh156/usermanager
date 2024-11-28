package com.infodation.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

//---When starting the project for the first time, please implement CommandLineRunner
// Add UserRepository to add Admin User
//	Uncomment the function below and start this project

// ---When starting the project for the second time.
// Remove the implement and comment out the function below

//	@Override
//	public void run(String... args) throws Exception {
//		User user = new User();
//		user.setUsername("admin");
//		user.setPassword(passwordEncoder.encode("admin"));
//		user.setEmail("binhdiep15963@gmail.com");
//		user.setUserId("12312314");
//		user.setFirstName("binh");
//		user.setLastName("diep");
//		userRepository.save(user);
//		System.out.println(user);
//	}

}
