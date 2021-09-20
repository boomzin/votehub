package org.boomzin.votehub.repository;

import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Vote;
import org.boomzin.votehub.to.VoteTo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id=:id")
    int delete(int id);

    @Query("SELECT v AS vote, v.restaurant.id AS restaurantId FROM Vote v WHERE v.user.id=?2 AND v.id=?1")
    Optional<VoteTo> get(int id, int userId);

    @Query("SELECT v FROM Vote v WHERE v.id=?1 AND v.user.id=?2 AND v.date=CURRENT_DATE")
    Optional<Vote> isExistToday(int id, int userId);

    @Query("SELECT v AS vote, v.restaurant.id AS restaurantId FROM Vote v WHERE v.user.id=?1 AND v.date=?2")
    Optional<VoteTo> getByDate (int userId, LocalDate date);

    @Query("SELECT v AS vote, v.restaurant.id AS restaurantId FROM Vote v WHERE v.user.id=?1")
    List<VoteTo> getAll(int userId);
}
