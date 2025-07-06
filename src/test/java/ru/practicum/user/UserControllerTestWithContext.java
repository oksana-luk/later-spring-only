package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.config.WebConfig;

@SpringJUnitWebConfig({UserController.class, UserControllerTestConfig.class, WebConfig.class})
public class UserControllerTestWithContext {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService;
    private MockMvc mvc;
    private UserDto userDto;

    @Autowired
    UserControllerTestWithContext(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        userDto = makeUserDto(1L, "oksana1@yande.ru", "oksana1", "chuff1");
    }

    public UserDto makeUserDto(Long id, String email, String firstName, String lastName) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setState(UserState.ACTIVE);
        return userDto;
    }
}
