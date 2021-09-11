package org.boomzin.votehub.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.boomzin.votehub.HasIdAndEmail;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

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

    public boolean addOrRemoveRole(Role role) {
        return roles.contains(role) ? roles.remove(role) : roles.add(role);
    }
}