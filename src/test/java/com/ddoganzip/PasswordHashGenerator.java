package com.ddoganzip;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String userPassword = "test1234";
        String staffPassword = "staff1234";

        System.out.println("User password hash (test1234): " + encoder.encode(userPassword));
        System.out.println("Staff password hash (staff1234): " + encoder.encode(staffPassword));
    }
}
