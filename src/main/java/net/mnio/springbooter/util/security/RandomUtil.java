package net.mnio.springbooter.util.security;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomUtil {

    private RandomUtil() {
    }

    public static String generateToken() {
        return RandomStringUtils.randomAlphanumeric(40);
    }

    static byte[] generateSalt() {
        return RandomStringUtils.randomAlphanumeric(10).getBytes();
    }

    public static String generateExceptionId() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
