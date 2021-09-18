package org.boomzin.votehub.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.boomzin.votehub.HasIdAndEmail;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class User extends BaseEntity implements HasIdAndEmail {

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotEmpty
    @NotBlank
    @Size(max = 128)
    private String email;

    @Column(name = "username")
    @Size(max = 128)
    @NotBlank
    private String username;

    @Column(name = "password")
    @Size(min = 4, max = 256)
    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique")})
    @Column(name = "role")
    @JoinColumn(name = "user_id")
    @ElementCollection(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("date DESC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    @ToString.Exclude
    private List<Vote> votes;

    public User(int id, String username, String email, String password, Role role, Role... roles) {
        this(id, username, email, password, EnumSet.of(role, roles));
    }

    public User(Integer id, String username, String email, String password, Collection<Role> roles) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        setRoles(roles);
    }
    public boolean addOrRemoveRole(Role role) {
        return roles.contains(role) ? roles.remove(role) : roles.add(role);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

}