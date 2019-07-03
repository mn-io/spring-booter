package net.mnio.springbooter.util.security;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
 * Technically speaking assertNotEquals could fail once in a blue moon because it's suppose to be random
 */
public class RandomUtilTest {

    @Test
    public void generateToken() {
        assertTrue(StringUtils.isNotBlank(RandomUtil.generateToken()));
        assertNotEquals(RandomUtil.generateToken(), RandomUtil.generateToken());
    }

    @Test
    public void generateSalt() {
        assertTrue(ArrayUtils.isNotEmpty(RandomUtil.generateSalt()));
        assertNotEquals(RandomUtil.generateSalt(), RandomUtil.generateSalt());
    }

    @Test
    public void generateExceptionId() {
        assertTrue(StringUtils.isNotBlank(RandomUtil.generateExceptionId()));
        assertNotEquals(RandomUtil.generateExceptionId(), RandomUtil.generateExceptionId());
    }
}