package org.boomzin.votehub.model;

import lombok.*;

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
public class Vote extends BaseEntity {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    @Size(min = 2, max = 128)
    private String name;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private User user ;
}