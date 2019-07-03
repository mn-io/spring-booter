package net.mnio.springbooter.util.security;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PasswordUtilUnitTest {

    private final static String PWD = " password 123 !$ äöü |\"";
    private final static String HASH = "$shiro1$SHA-512$55000$dnhUYTdlU3FLVA==$ajlM7OVwOZnjF0ErWaQLFfrOy3vOaYv5RgT/yDzdWCrUX8y18NY/UOTUVvoWPYNVElnhjMImPxie7QKwbh7nqg==";

    @Test
    public void hashPasswordsWithDifferentSalts() {
        final String hash1 = PasswordUtil.hashPasswordWithSalt(PWD);
        final String hash2 = PasswordUtil.hashPasswordWithSalt(PWD);
        assertNotEquals(hash1, hash2);
        assertTrue(StringUtils.isNotBlank(hash1));
    }

    @Test
    public void hashPasswordsBruteForce() {
        final String[] pwds = {" ", "  ", "../", "!", "&", "<script>", HASH, PWD};
        final Set<String> hashSet = new HashSet<>();
        for (String pwd : pwds) {
            final String hash = PasswordUtil.hashPasswordWithSalt(pwd);
            System.out.printf("Pwd: %s, Hash: %s%n", pwd, hash);
            assertTrue(StringUtils.isNotBlank(hash));
            assertFalse(hashSet.contains(hash));
            hashSet.add(hash);
        }
    }

    @Test
    public void hashPasswordEmpty() {
        assertNull(PasswordUtil.hashPasswordWithSalt(""));
        assertNull(PasswordUtil.hashPasswordWithSalt(null));
    }

    @Test
    public void checkPassword() {
        assertTrue(PasswordUtil.checkPassword(PWD, HASH));
    }

    @Test
    public void checkPasswordEmpty() {
        assertFalse(PasswordUtil.checkPassword("", HASH));
        assertFalse(PasswordUtil.checkPassword(PWD, ""));
        assertFalse(PasswordUtil.checkPassword("", ""));
        assertFalse(PasswordUtil.checkPassword(null, HASH));
        assertFalse(PasswordUtil.checkPassword(PWD, null));
        assertFalse(PasswordUtil.checkPassword(null, null));
    }

}