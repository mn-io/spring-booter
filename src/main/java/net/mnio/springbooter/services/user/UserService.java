package net.mnio.springbooter.services.user;

import net.mnio.jOrchestra.InterruptService;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.security.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private InterruptService interruptService;

    public Page<User> findAll(final Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public User createUser(final UserCreateOrUpdateDto dto) {
        final User user = new User();
        return updateUser(user, dto);
    }

    public boolean checkPassword(final User user, final String plaintextPassword) {
        return PasswordUtil.checkPassword(plaintextPassword, user.getPassword());
    }

    @Transactional
    public User updateUser(final User user, final UserCreateOrUpdateDto dto) {
        user.setEmail(StringUtils.stripToEmpty(dto.getEmail()).toLowerCase());
        user.setName(StringUtils.stripToEmpty(dto.getName()));

        if (StringUtils.isEmpty(user.getEmail())) {
            throw new IllegalArgumentException("e-mail must not be empty");
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            throw new IllegalArgumentException("password must not be empty");
        }

        final String encode = PasswordUtil.hashPasswordWithSalt(dto.getPassword());
        user.setPassword(encode);

        interruptService.interrupt();
        return userRepository.save(user);
    }

    @Transactional
    public void delete(final User user) {
        final List<UserSession> sessions = userSessionRepository.findAllByUserId(user.getId());
        userSessionRepository.deleteAll(sessions);

        userRepository.delete(user);
    }
}
