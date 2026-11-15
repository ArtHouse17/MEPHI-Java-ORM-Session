package ru.ormcourse.academy.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ormcourse.academy.dto.AccountDtos.Create;
import ru.ormcourse.academy.dto.AccountDtos.ProfileInput;
import ru.ormcourse.academy.dto.AccountDtos.ProfileView;
import ru.ormcourse.academy.dto.AccountDtos.Update;
import ru.ormcourse.academy.dto.AccountDtos.UserView;
import ru.ormcourse.academy.service.AccountService;

@RestController
@RequestMapping("/api/users")
public class AccountController {
    private final AccountService accounts;

    public AccountController(AccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping
    List<UserView> list() {
        return accounts.list();
    }

    @GetMapping("/{id}")
    UserView get(@PathVariable @Positive long id) {
        return accounts.get(id);
    }

    @PostMapping
    ResponseEntity<UserView> create(@Valid @RequestBody Create input) {
        UserView user = accounts.create(input);
        return Responses.created(user.id(), user);
    }

    @PutMapping("/{id}")
    UserView update(@PathVariable @Positive long id, @Valid @RequestBody Update input) {
        return accounts.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @Positive long id) {
        accounts.delete(id);
    }

    @GetMapping("/{id}/profile")
    ProfileView profile(@PathVariable @Positive long id) {
        return accounts.profile(id);
    }

    @PutMapping("/{id}/profile")
    ProfileView updateProfile(@PathVariable @Positive long id, @Valid @RequestBody ProfileInput input) {
        return accounts.updateProfile(id, input);
    }
}
