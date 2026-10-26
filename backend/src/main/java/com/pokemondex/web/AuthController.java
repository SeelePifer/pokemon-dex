package com.pokemondex.web;

import com.pokemondex.application.LoginUseCase;
import com.pokemondex.application.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final LoginUseCase login;

    public AuthController(RegisterUserUseCase registerUser, LoginUseCase login) {
        this.registerUser = registerUser;
        this.login = login;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return HttpMapper.user(registerUser.execute(new RegisterUserUseCase.Command(
                request.email(),
                request.password(),
                request.displayName()
        )));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.Result result = login.execute(request.email(), request.password());
        return new LoginResponse(
                result.token().accessToken(),
                result.token().expiresInSeconds(),
                HttpMapper.user(result.user())
        );
    }
}
