package com.bloodmatcher.user;

import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long id) {
        return UserDto.from(getUserById(id));
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByEmail(String email) {
        return UserDto.from(getUserByEmail(email));
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Role role, Pageable pageable) {
        Page<User> users = (role != null)
                ? userRepository.findByRole(role, pageable)
                : userRepository.findAll(pageable);
        return users.map(UserDto::from);
    }

    @Transactional
    public UserDto updateUserStatus(Long userId, boolean enabled) {
        User user = getUserById(userId);
        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }
}
