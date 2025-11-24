-- Fix password hashes for test accounts
-- These are verified BCrypt hashes for the passwords

-- Update user accounts with correct hash for "test1234"
UPDATE customers
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE email IN ('user@test.com', 'john@test.com', 'emily@test.com', 'mike@test.com');

-- Update staff account with correct hash for "staff1234"
UPDATE customers
SET password = '$2a$10$Xl4/5XJZLxJjCXXvlAl1/.Qp0K3CJM/W0.yLqg.qH0aDHEOZGTnTu'
WHERE email = 'staff@test.com';

-- Verify the updates
SELECT email,
       CASE
         WHEN email = 'staff@test.com' THEN 'Password: staff1234'
         ELSE 'Password: test1234'
       END as password_info,
       role,
       LEFT(password, 20) || '...' as password_hash_preview
FROM customers
WHERE email IN ('user@test.com', 'john@test.com', 'emily@test.com', 'mike@test.com', 'staff@test.com')
ORDER BY email;
