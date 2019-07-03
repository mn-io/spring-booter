package net.mnio.springbooter;

import net.mnio.springbooter.persistence.model.User;

public class TestUtil {
    public static User createUser(final String email, final String name, final String pwd) {
        final User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pwd);
        return user;
    }
}
