package ru.ormcourse.academy.accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "user_profile")
public class UserProfile extends IdentifiedEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private UserAccount account;

    @Column(length = 2000)
    private String bio;

    @Column(length = 1000)
    private String avatarUrl;

    protected UserProfile() {
    }

    public UserProfile(UserAccount account, String bio, String avatarUrl) {
        this.account = account;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public UserAccount getAccount() {
        return account;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void update(String bio, String avatarUrl) {
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
}
