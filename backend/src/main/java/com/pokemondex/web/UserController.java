package com.pokemondex.web;

import com.pokemondex.application.DeleteUserUseCase;
import com.pokemondex.application.GetCurrentUserUseCase;
import com.pokemondex.application.ListUsersUseCase;
import com.pokemondex.application.UpdateCurrentUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetCurrentUserUseCase getCurrentUser;
    private final UpdateCurrentUserUseCase updateCurrentUser;
    private final ListUsersUseCase listUsers;
    private final DeleteUserUseCase deleteUser;

    public UserController(
            GetCurrentUserUseCase getCurrentUser,
            UpdateCurrentUserUseCase updateCurrentUser,
            ListUsersUseCase listUsers,
            DeleteUserUseCase deleteUser
    ) {
        this.getCurrentUser = getCurrentUser;
        this.updateCurrentUser = updateCurrentUser;
        this.listUsers = listUsers;
        this.deleteUser = deleteUser;
    }

    @GetMapping("/me")
    public UserResponse me() {
        return HttpMapper.user(getCurrentUser.execute(CurrentUser.require().id()));
    }

    @PatchMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return HttpMapper.user(updateCurrentUser.execute(CurrentUser.require().id(), request.displayName()));
    }

    @GetMapping
    public List<UserResponse> list() {
        return listUsers.execute().stream().map(HttpMapper::user).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUser.execute(CurrentUser.require().id(), id);
        return ResponseEntity.noContent().build();
    }
}
