package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.config.WebConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig({UserController.class, UserControllerTestConfig.class, WebConfig.class})
public class UserControllerWithWebContextTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService;
    private MockMvc mvc;
    private UserDto userDto;

    @Autowired
    UserControllerWithWebContextTest(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        userDto = makeUserDto(1L, "oksana1@yande.ru", "oksana1", "chuff1");
        Mockito.reset(userService);
    }

    @Test
    public void saveUser() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.registrationDate", is(userDto.getRegistrationDate())))
                .andExpect(jsonPath("$.state", is(userDto.getState().getValue())));
    }

    @Test
    public void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(makeUserDto(2L, "oksana2@yande.ru", "oksana2", "chuff2"));
        users.add(makeUserDto(3L, "oksana3@yande.ru", "oksana3", "chuff3"));

        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].email", is(users.getFirst().getEmail())))
                .andExpect(jsonPath("$.[1].email", is(users.get(1).getEmail())));

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
