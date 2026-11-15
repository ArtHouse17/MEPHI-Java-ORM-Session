package ru.ormcourse.academy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.ormcourse.academy.entity.Role;
import ru.ormcourse.academy.entity.UserAccount;
import ru.ormcourse.academy.entity.UserProfile;

public final class AccountDtos {
    private AccountDtos() {
    }

    public record Create(
            @NotBlank @Size(max = 160) String name,
            @NotBlank @Email @Size(max = 254) String email,
            @NotNull Role role) {
    }

    public record Update(
            @NotBlank @Size(max = 160) String name,
            @NotBlank @Email @Size(max = 254) String email) {
    }

    public record ProfileInput(
            @Size(max = 2000) String bio,
            @Size(max = 1000) @Pattern(regexp = "https?://[^\\s/]+[^\\s]*") String avatarUrl) {
    }

    public record UserView(long id, String name, String email, Role role) {
        public static UserView from(UserAccount account) {
            return new UserView(account.getId(), account.getName(), account.getEmail(), account.getRole());
        }
    }

    public record ProfileView(long userId, String bio, String avatarUrl) {
        public static ProfileView from(UserAccount account) {
            UserProfile profile = account.getProfile();
            return new ProfileView(account.getId(), profile.getBio(), profile.getAvatarUrl());
        }
    }
}
