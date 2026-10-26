package com.pokemondex.web;

import com.pokemondex.application.DeleteUserUseCase;
import com.pokemondex.application.GetCurrentUserUseCase;
import com.pokemondex.application.ListUsersUseCase;
import com.pokemondex.application.LoginUseCase;
import com.pokemondex.application.RegisterUserUseCase;
import com.pokemondex.application.UpdateCurrentUserUseCase;
import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.PasswordHasher;
import com.pokemondex.domain.port.TokenIssuer;
import com.pokemondex.domain.port.UserRepository;
import com.pokemondex.infrastructure.config.JwtProperties;
import com.pokemondex.infrastructure.security.JwtTokenAdapter;
import com.pokemondex.infrastructure.security.SecurityConfig;
import com.pokemondex.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UserController.class}, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({
        SecurityConfig.class,
        JwtTokenAdapter.class,
        RegisterUserUseCase.class,
        LoginUseCase.class,
        GetCurrentUserUseCase.class,
        UpdateCurrentUserUseCase.class,
        ListUsersUseCase.class,
        DeleteUserUseCase.class
})
@EnableConfigurationProperties(JwtProperties.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenIssuer tokens;

    @MockitoBean
    UserRepository users;

    @MockitoBean
    PasswordHasher hasher;

    @Test
    void registerRejectsAShortPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@pokemon.local","password":"short","displayName":"Ada"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        when(users.existsByEmail("ada@pokemon.local")).thenReturn(true);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@pokemon.local","password":"secret123","displayName":"Ada"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void badCredentialsReturn401() throws Exception {
        UserAccount user = TestSupport.user(Role.USER);
        when(users.findByEmail("trainer@pokemon.local")).thenReturn(Optional.of(user));
        when(hasher.matches(any(), any())).thenReturn(false);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"trainer@pokemon.local","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aUserCannotListAccounts() throws Exception {
        UserAccount user = UserAccount.create("demo@pokemon.local", "Demo", "hash", Role.USER);
        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void anAdminCanListAccounts() throws Exception {
        UserAccount admin = UserAccount.create("admin@pokemon.local", "Admin", "hash", Role.ADMIN);
        when(users.findAll()).thenReturn(List.of(admin));
        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void anAdminCannotDeleteTheirOwnAccount() throws Exception {
        UserAccount admin = UserAccount.create("admin@pokemon.local", "Admin", "hash", Role.ADMIN);
        mockMvc.perform(delete("/api/v1/users/" + admin.id()).header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest());
    }

    private String bearer(UserAccount user) {
        return "Bearer " + tokens.issue(user).accessToken();
    }
}
