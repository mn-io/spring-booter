package net.mnio.springbooter.services.user;

import net.mnio.springbooter.controller.api.UserSignupDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.util.security.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(final UserSignupDto userSignupDto) {
        if (StringUtils.isEmpty(userSignupDto.getEmail())) {
            throw new IllegalArgumentException("e-mail must not be empty");
        }

        if (StringUtils.isEmpty(userSignupDto.getName())) {
            throw new IllegalArgumentException("name must not be empty");
        }

        if (StringUtils.isEmpty(userSignupDto.getPassword())) {
            throw new IllegalArgumentException("password must not be empty");
        }

        final User user = new User();
        user.setName(userSignupDto.getName());
        user.setEmail(userSignupDto.getEmail());

        final String encode = PasswordUtil.hashPasswordWithSalt(userSignupDto.getPassword());
        user.setPassword(encode);
        return userRepository.save(user);
    }

    public void setPassword(final User user, final String newPlaintextPassword) {
        final String encoded = PasswordUtil.hashPasswordWithSalt(newPlaintextPassword);
        user.setPassword(encoded);
        userRepository.save(user);
    }

    public boolean checkPassword(final User user, final String plaintextPassword) {
        return PasswordUtil.checkPassword(plaintextPassword, user.getPassword());
    }

    public void deleteUser(final User user) {
        userRepository.delete(user);
    }
}
