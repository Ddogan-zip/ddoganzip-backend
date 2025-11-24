package com.ddoganzip.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for data.sql
 * Run this to get correct password hashes for test accounts
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate hashes for test accounts
        String userPassword = "test1234";
        String staffPassword = "staff1234";

        String userHash = encoder.encode(userPassword);
        String staffHash = encoder.encode(staffPassword);

        System.out.println("=== BCrypt Password Hashes for data.sql ===\n");

        System.out.println("User Account (user@test.com):");
        System.out.println("Password: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println();

        System.out.println("Staff Account (staff@test.com):");
        System.out.println("Password: " + staffPassword);
        System.out.println("Hash: " + staffHash);
        System.out.println();

        System.out.println("=== SQL Update Statements ===\n");
        System.out.println("-- For user@test.com");
        System.out.println("UPDATE customers SET password = '" + userHash + "' WHERE email = 'user@test.com';");
        System.out.println();
        System.out.println("-- For staff@test.com");
        System.out.println("UPDATE customers SET password = '" + staffHash + "' WHERE email = 'staff@test.com';");
    }
}
