package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mappToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }
}
