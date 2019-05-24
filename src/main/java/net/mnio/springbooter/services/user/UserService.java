package net.mnio.springbooter.services.user;

import net.mnio.springbooter.controller.api.UserUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.log.Log;
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
    private UserVerificationService userVerificationService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUser(final String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

//    @Transactional
//    public User createOrUpdateUser(final UserDto userDto) {
//        final User user;
//        final String newPassword;
//        if (StringUtils.isEmpty(userDto.getId())) {
//            newPassword = RandomUtil.generateRandomPassword();
//            user = userVerificationService.createUser(userDto.getEmail(), newPassword);
//        } else {
//            user = userRepository.findById(userDto.getId()).get();
//            // TODO: proper catch
//            if (user == null) {
//                throw new IllegalStateException("user by id and accountId not found");
//            }
//            user.setEmail(userDto.getEmail());
//        }
//
//        final User saved = userRepository.save(user);
//        return saved;
//    }

    //TODO: test
    @Transactional
    public void delete(final String userId) {
        final Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return;
        }

        final List<UserSession> sessions = userSessionRepository.findAllByUserId(user.get().getId());
        userSessionRepository.deleteAll(sessions);

        userRepository.delete(user.get());
    }

    public Page<User> getAll(final Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User updateUser(final User user, final UserUpdateDto userUpdateDto) {
        user.setEmail(userUpdateDto.getEmail());
        user.setName(userUpdateDto.getName());

        return userRepository.save(user);
    }
}
