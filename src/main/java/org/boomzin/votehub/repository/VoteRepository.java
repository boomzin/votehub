package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.Vote;
import org.boomzin.votehub.to.VoteTo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.id=:id")
    int delete(int id);

    @Query("SELECT v AS vote, v.restaurant.id AS restaurantId FROM Vote v WHERE v.user.id=?1 AND v.id=?2")
    Optional<VoteTo> getById(int userId, int id);

    @Query("SELECT v FROM Vote v WHERE v.user.id=?1 AND v.date=CURRENT_DATE")
    Optional<Vote> getActual (int userId);

    @Query("SELECT v AS vote, v.restaurant.id AS restaurantId FROM Vote v WHERE v.user.id=?1")
    Optional<List<VoteTo>> getAll(int userId);
}
