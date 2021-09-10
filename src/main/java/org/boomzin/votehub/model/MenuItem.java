package org.boomzin.votehub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "menu_item", uniqueConstraints = @UniqueConstraint(name = "menu_item_unique_date_description",
        columnNames = {"date", "description"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class MenuItem extends BaseEntity {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    @Size(min = 2, max = 128)
    private String name;

    @Column(name = "price")
    @Size(min = 1, max = 10000)
    private Integer price;

    @ManyToOne
    @JsonBackReference(value = "restaurant-menus")
    private Restaurant restaurant;
}
