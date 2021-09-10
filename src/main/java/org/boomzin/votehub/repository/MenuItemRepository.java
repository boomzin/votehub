package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.id=?2 AND mi.date=CURRENT_DATE")
    int deleteActualItemForRestaurant(int resaurantId, int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.date=CURRENT_DATE ")
    int deleteAllActualItemsForRestaurant(int id);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.id=?2 AND mi.date=CURRENT_DATE")
    Optional<MenuItem> getActualMenuIteForCurrentRestaurant(int restaurantId, int id);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.date=CURRENT_DATE")
    Optional<List<MenuItem>> getActualMenuCurrentRestaurant(int restaurantId);
}
