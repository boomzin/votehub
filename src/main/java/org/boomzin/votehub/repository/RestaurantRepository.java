package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.to.RestaurantIdWithRating;
import org.springframework.data.jpa.repository.EntityGraph;
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
    Optional<Restaurant> getWithMenuOnDate(int id, LocalDate date);

    @Query("SELECT r FROM Restaurant r WHERE r.name=?1 AND r.address=?2")
    Optional<Restaurant> getByNameAndAddress(String name, String address);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.menu m WHERE m.date=?1")
    List<Restaurant> getAllWithMenuOnDate(LocalDate date);

    @EntityGraph(attributePaths = {"votes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r WHERE r.id=?1")
    Optional<Restaurant> getWithVotes(int id);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.votes v WHERE r.id=?1 AND v.date=?2")
    Optional<Restaurant> getWithVotesOnDate(int id, LocalDate date);

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.votes v WHERE v.date=?1")
    List<Restaurant> getAllWithVotesOnDate(LocalDate date);

//    https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions
    @Query("SELECT DISTINCT r.id AS restaurantId, count (v) as rating " +
            "FROM Restaurant r LEFT JOIN r.votes v WHERE v.date=?1 GROUP BY restaurantId ORDER BY rating DESC")
    List<RestaurantIdWithRating> getRatingOnDate(LocalDate date);
}
