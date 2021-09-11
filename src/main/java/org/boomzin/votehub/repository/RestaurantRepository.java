package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.to.RestaurantWithRating;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant>  {

    @EntityGraph(attributePaths = {"menu"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r WHERE r.id=?1")
    Optional<Restaurant> getWithMenus(int id);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.menu m WHERE r.id=?1 AND m.date=?2")
    Optional<Restaurant> getWithMenusOnDate(int id, LocalDate date);

    @Query("SELECT r FROM Restaurant r WHERE r.name=?1 AND r.address=?2")
    Optional<Restaurant> getByNameAndAddress(String name, String address);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.menu m WHERE r.id=?1 AND m.date=CURRENT_DATE")
    Optional<Restaurant> getWithActualMenu(int id);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.menu m WHERE m.date=?1")
    Optional<List<Restaurant>> getAllWithMenuOnDate(LocalDate date);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.menu m WHERE m.date=CURRENT_DATE")
    Optional<List<Restaurant>> getAllWithActualMenu();

    @EntityGraph(attributePaths = {"votes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r WHERE r.id=?1")
    Optional<Restaurant> getWithVotes(int id);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.votes v WHERE r.id=?1 AND v.date=?2")
    Optional<Restaurant> getWithVotesOnDate(int id, LocalDate date);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.votes v WHERE r.id=?1 AND v.date=CURRENT_DATE")
    Optional<Restaurant> getWithActualVotes(int id);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.votes v WHERE v.date=?1")
    Optional<List<Restaurant>> getAllWithVotesOnDate(LocalDate date);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.votes v WHERE v.date=CURRENT_DATE")
    Optional<List<Restaurant>> getAllWithActualVotes();

//    https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions
    @Query("SELECT DISTINCT r AS restaurantInRating, count (v) as rating " +
            "FROM Restaurant r LEFT JOIN r.votes v WHERE v.date=?1 GROUP BY r ORDER BY rating DESC")
    Optional<List<RestaurantWithRating>> getRatingOnDate(LocalDate date);

    @Query("SELECT DISTINCT r AS restaurantInRating, count (v) as rating " +
            "FROM Restaurant r LEFT JOIN r.votes v WHERE v.date=CURRENT_DATE GROUP BY r ORDER BY rating DESC")
    Optional<List<RestaurantWithRating>> getActualRating();
}
