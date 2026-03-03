-- =====================================================
-- Grant ROLE_ADMIN All Permissions
-- Flyway Migration V4
-- =====================================================
-- This migration ensures ROLE_ADMIN has all three permissions:
-- USER_MANAGE, PRODUCT_MANAGE, and SALE_EXECUTE
-- This gives Manager role full access to all APIs
-- =====================================================

-- Insert USER_MANAGE permission for ROLE_ADMIN if not already present
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name = 'USER_MANAGE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Verify ROLE_ADMIN has PRODUCT_MANAGE (should already exist)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name = 'PRODUCT_MANAGE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Verify ROLE_ADMIN has SALE_EXECUTE (should already exist)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name = 'SALE_EXECUTE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =====================================================
-- END OF MIGRATION V4
-- =====================================================
