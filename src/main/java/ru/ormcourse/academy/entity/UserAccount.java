package ru.ormcourse.academy.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class UserAccount extends IdentifiedEntity {
    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private UserProfile profile;

    protected UserAccount() {
    }

    private UserAccount(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserAccount register(String name, String email, Role role) {
        UserAccount account = new UserAccount(name, email, role);
        account.profile = new UserProfile(account, null, null);
        return account;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
