package org.boomzin.votehub.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "restaurant", uniqueConstraints = @UniqueConstraint(name = "restaurant_unique_name_address",
        columnNames = {"restaurant_name", "address"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Restaurant extends BaseEntity {

    @Column(name = "restaurant_name")
    @Size(max = 128)
    private String name;

    @Column(name = "address")
    @Size(max = 128)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OrderBy("date DESC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<MenuItem> menu;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OrderBy("date DESC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<Vote> votes;
}
