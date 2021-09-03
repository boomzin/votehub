package org.boomzin.votehub.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "restaurant")
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
}
