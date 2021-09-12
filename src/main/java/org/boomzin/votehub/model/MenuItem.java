package org.boomzin.votehub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.boomzin.votehub.HasId;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "menu_item", uniqueConstraints = @UniqueConstraint(name = "menu_item_unique_date_description",
        columnNames = {"restaurant_id", "menu_date", "description"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class MenuItem extends BaseEntity implements HasId {

    @Column(name = "menu_date")
    @NotNull
    private LocalDate date;

    @Column(name = "description")
    @Size(min = 2, max = 128)
    @NotBlank
    private String description;

    @Column(name = "price")
    @Range(min = 1, max = 10000)
    private Integer price;

    @ManyToOne
    @JsonBackReference(value = "restaurant-menus")
    private Restaurant restaurant;
}
