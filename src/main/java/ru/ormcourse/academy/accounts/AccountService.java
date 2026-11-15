package ru.ormcourse.academy.accounts;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.accounts.AccountDtos.Create;
import ru.ormcourse.academy.accounts.AccountDtos.ProfileInput;
import ru.ormcourse.academy.accounts.AccountDtos.ProfileView;
import ru.ormcourse.academy.accounts.AccountDtos.Update;
import ru.ormcourse.academy.accounts.AccountDtos.UserView;
import ru.ormcourse.academy.common.ApiException;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final UserAccountRepository accounts;

    public AccountService(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    public List<UserView> list() {
        return accounts.findAllByOrderByIdAsc().stream().map(UserView::from).toList();
    }

    public UserView get(long id) {
        return UserView.from(requireAccount(id));
    }

    @Transactional
    public UserView create(Create input) {
        UserAccount account = UserAccount.register(input.name().strip(), normalizeEmail(input.email()), input.role());
        return UserView.from(accounts.save(account));
    }

    @Transactional
    public UserView update(long id, Update input) {
        UserAccount account = requireAccount(id);
        account.update(input.name().strip(), normalizeEmail(input.email()));
        return UserView.from(account);
    }

    @Transactional
    public void delete(long id) {
        accounts.delete(requireAccount(id));
    }

    public ProfileView profile(long id) {
        return ProfileView.from(requireWithProfile(id));
    }

    @Transactional
    public ProfileView updateProfile(long id, ProfileInput input) {
        UserAccount account = requireWithProfile(id);
        account.getProfile().update(input.bio(), input.avatarUrl());
        return ProfileView.from(account);
    }

    public UserAccount requireAccount(long id) {
        return ApiException.require(accounts.findById(id), "User", id);
    }

    public UserAccount requireRole(long id, Role role) {
        UserAccount account = requireAccount(id);
        if (account.getRole() != role) {
            throw ApiException.invalid("User #" + id + " must have role " + role);
        }
        return account;
    }

    private UserAccount requireWithProfile(long id) {
        return ApiException.require(accounts.findWithProfileById(id), "User", id);
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
