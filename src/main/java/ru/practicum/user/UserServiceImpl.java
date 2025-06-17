package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final UserMapper userMapper = new UserMapper();

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::apply).toList();
    }

    @Override
    public UserDto saveUser(UserCreateDto userCreateDto) {
        User user = userMapper.mappToUser(userCreateDto);
        return userMapper.apply(userRepository.save(user));
    }
}
