package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    UserDto userDto;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();

        userDto = makeUserDto(1L, "oksana1@yande.ru", "oksana1", "chuff1");
    }

    @Test
    void saveNewUser() throws Exception {
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
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(makeUserDto(1L, "oksana1@yande.ru", "oksana1", "chuff1"));
        users.add(makeUserDto(2L, "oksana2@yande.ru", "oksana2", "chuff2"));

        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].email", is(users.get(0).getEmail())))
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
