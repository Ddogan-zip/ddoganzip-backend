-- Generate correct BCrypt hashes by copying from a working account
--
-- Step 1: Create temporary accounts with correct passwords
-- Step 2: Copy their hashes to test accounts
-- Step 3: Delete temporary accounts

-- Create temporary account with password "test1234"
-- (This will be done via API call, not SQL, because we need BCrypt encoding)

-- After creating temp account via /api/auth/register with password "test1234":
-- Run this to copy the hash:

-- Get the hash from the newest account (just created via registration)
SELECT 'Copy this hash for test1234:' as instruction, password
FROM customers
WHERE email = 'temp_test1234@example.com';

-- Update all test accounts with the correct hash
-- UPDATE customers
-- SET password = (SELECT password FROM customers WHERE email = 'temp_test1234@example.com' LIMIT 1)
-- WHERE email IN ('user@test.com', 'john@test.com', 'emily@test.com', 'mike@test.com');

-- Clean up
-- DELETE FROM customers WHERE email = 'temp_test1234@example.com';
