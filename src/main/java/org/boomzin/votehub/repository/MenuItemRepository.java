package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.MenuItem;
import org.boomzin.votehub.to.MenuTo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuItemRepository extends BaseRepository<MenuItem>  {

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.date=?2")
    void deleteAllMenuItemsForRestaurantOnDate(int restaurantId, LocalDate date);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.id=?2 AND mi.date=?3")
    Optional<MenuItem> getMenuItemsForCurrentRestaurantOnDate(int restaurantId, int id, LocalDate date);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.date=?2")
    List<MenuItem> getMenuCurrentRestaurantOnDate(int restaurantId, LocalDate date);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=?1 AND mi.date=?2")
    Optional<MenuItem> getMenuItemCurrentRestaurantOnDate(int restaurantId, LocalDate date);

    @Query("SELECT mi AS menuItem, mi.restaurant.id AS restaurantId FROM MenuItem mi WHERE mi.date=?1")
    List<MenuTo> getMenusOnDate(LocalDate date);
}
