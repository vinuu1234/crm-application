package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@SpringBootApplication
@EnableTransactionManagement
public class CrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
        System.out.println("=".repeat(60));
        System.out.println("CRM Application Started Successfully with MySQL!");
        System.out.println("=".repeat(60));
        System.out.println("Access at: http://localhost:8080");
        System.out.println("\nDefault Login Credentials:");
        System.out.println("Admin:    admin@crm.com / admin123");
        System.out.println("Manager:  manager@crm.com / manager123");
        System.out.println("Employee: employee@crm.com / emp123");
        System.out.println("=".repeat(60));
       
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String correctHash = encoder.encode("admin123");
                System.out.println(correctHash);
         
    }
}