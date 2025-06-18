package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    private List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    private UserDto saveNewUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }
}
