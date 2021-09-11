package org.boomzin.votehub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.boomzin.votehub.HasId;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Table(name = "vote", uniqueConstraints = @UniqueConstraint(name = "votes_date_userId",
        columnNames = {"date", "user_id"}))
@Entity

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Vote extends BaseEntity implements HasId {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    @Size(min = 2, max = 128)
    private String description;

    @ManyToOne
    @JsonBackReference(value = "restaurant-votes")
    private Restaurant restaurant;

    @ManyToOne
    @JsonBackReference
    private User user ;
}