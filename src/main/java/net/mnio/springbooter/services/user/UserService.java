package net.mnio.springbooter.services.user;

import net.mnio.jOrchestra.InterruptService;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.log.Log;
import net.mnio.springbooter.util.security.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Log
    private Logger log;

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
        updateUser(user, dto);
        return userRepository.save(user);
    }

    public boolean checkPassword(final User user, final String plaintextPassword) {
        return PasswordUtil.checkPassword(plaintextPassword, user.getPassword());
    }

    @Transactional
    public User updateUser(final User user, final UserCreateOrUpdateDto dto) {
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        final String encode = PasswordUtil.hashPasswordWithSalt(dto.getPassword());
        user.setPassword(encode);

        check(user);

        interruptService.interrupt();
        return userRepository.save(user);
    }

    @Transactional
    public void delete(final User user) {
        final List<UserSession> sessions = userSessionRepository.findAllByUserId(user.getId());
        userSessionRepository.deleteAll(sessions);

        userRepository.delete(user);
    }

    private void check(final User user) {
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new IllegalArgumentException("e-mail must not be empty");
        }

        if (StringUtils.isEmpty(user.getName())) {
            throw new IllegalArgumentException("name must not be empty");
        }

//        if (StringUtils.isEmpty(user.getPassword())) {
//            throw new IllegalArgumentException("password must not be empty");
//        }
    }
}
