package ttexpandapis.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserLoginRequestDto;
import ttexpandapis.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/clear-user-table.sql"));
        }
    }

    @Test
    public void testAddUser() throws Exception {
        CreateUserDto userDto = new CreateUserDto("username1","password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/add")
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username")
                                .value("username1"));
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        CreateUserDto userDto = new CreateUserDto("newUser","password1234");
        userService.register(userDto);
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(
                "newUser",
                "password1234");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/authenticate")
                        .content(asJsonString(loginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
    }

    @Test
    public void testInvalidAuthentication() throws Exception {
        CreateUserDto userDto = new CreateUserDto("newUser","password1234");
        userService.register(userDto);

        UserLoginRequestDto invalidLoginRequestDto = new UserLoginRequestDto(
                "newUser",
                "wrongPassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/authenticate")
                        .content(asJsonString(invalidLoginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(status().isForbidden());
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
