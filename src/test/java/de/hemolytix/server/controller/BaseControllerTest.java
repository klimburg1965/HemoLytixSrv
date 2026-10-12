package de.hemolytix.server.controller;

import de.hemolytix.server.config.JwtUtils;
import de.hemolytix.server.config.SecurityConfig;
import de.hemolytix.server.repository.UserRepository;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public abstract class BaseControllerTest {

    @MockBean
    protected JwtUtils jwtUtils;

    @MockBean
    protected UserRepository userRepository;
}