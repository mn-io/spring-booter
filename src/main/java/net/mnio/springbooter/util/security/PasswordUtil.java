package net.mnio.springbooter.util.security;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.HashRequest.Builder;
import org.apache.shiro.util.ByteSource;

public class PasswordUtil {

    final static DefaultPasswordService PASSWORD_SERVICE = new SaltedPasswordService();

    static {
        final PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(PASSWORD_SERVICE);

        final DefaultHashService hashService = new DefaultHashService();
        hashService.setHashIterations(55000);
        PASSWORD_SERVICE.setHashService(hashService);
    }

    public static boolean checkPassword(final String plainTextPassword, final String savedPassword) {
        return PASSWORD_SERVICE.passwordsMatch(plainTextPassword, savedPassword);
    }

    public static String hashPasswordWithSalt(final String plainTextPassword) {
        return PASSWORD_SERVICE.encryptPassword(plainTextPassword);
    }

    private static class SaltedPasswordService extends DefaultPasswordService {

        @Override
        protected HashRequest createHashRequest(final ByteSource plaintext) {
            final byte[] salt = RandomUtil.generateSalt();
            return new Builder()
                    .setSource(plaintext)
                    .setSalt(salt)
                    .build();
        }

        @Override
        protected HashRequest buildHashRequest(final ByteSource plaintext, final Hash saved) {
            return super.buildHashRequest(plaintext, saved);
        }
    }
}
