package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mappToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }
}
