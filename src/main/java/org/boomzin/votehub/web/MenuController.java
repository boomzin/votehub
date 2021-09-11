package org.boomzin.votehub.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.model.MenuItem;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.MenuItemRepository;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class MenuController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    @PostMapping(value = "/{restaurantId}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MenuItem>> createMenuWithLocation(@PathVariable int restaurantId, @RequestBody List<MenuItem> menu) {
        log.info("create menu for restaurant {} for today", restaurantId);
        menuItemRepository.deleteAllActualItemsForRestaurant(restaurantId);
        // TODO: add description_date unique validator
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        for (MenuItem menuItem : menu) {
            checkNew(menuItem);
            menuItem.setDate(LocalDate.now());
            menuItem.setDescription(menuItem.getDescription().toLowerCase());
            menuItem.setRestaurant(restaurant);
            menuItemRepository.save(menuItem);
        }
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menu")
                .buildAndExpand(restaurantId).toUri();
        return ResponseEntity.created(uriOfNewResource).body(menu);
    }

    @Transactional
    @DeleteMapping("/{restaurantId}/menu")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuForCurrentRestaurant(@PathVariable int restaurantId) {
        log.info("delete actual menu for restaurant {}", restaurantId);
        menuItemRepository.deleteAllActualItemsForRestaurant(restaurantId);
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItem> getActualMenuForCurrentRestaurant (@PathVariable int restaurantId) {
        log.info("get actual menu for restaurant {}", restaurantId);
        return menuItemRepository.getActualMenuCurrentRestaurant(restaurantId).get();
    }

    @Transactional
    @PutMapping(value = "/{restaurantId}/menu/menuitem/{menuItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void UpdateMenuItem(@PathVariable int restaurantId, @PathVariable int menuItemId, @RequestBody MenuItem menuItem) {
        log.info("update menu item {} for restaurant {} for today",menuItemId, restaurantId);
        assureIdConsistent(menuItem, menuItemId);
        Optional<Restaurant> restaurant = restaurantRepository.getWithActualMenu(restaurantId);
        if (restaurant.isPresent() && restaurant.get().getMenu().contains(menuItem)) {
            menuItem.setDate(LocalDate.now());
            menuItem.setDescription(menuItem.getDescription().toLowerCase());
            menuItem.setRestaurant(restaurant.get());
            menuItemRepository.save(menuItem);
        }
    }

    @Transactional
    @PostMapping(value = "/{restaurantId}/menu/menuitem", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuItem> createMenuItemWithLocation(@PathVariable int restaurantId, @RequestBody MenuItem menuItem) {
        log.info("create menu item for restaurant {} for today", restaurantId);
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        checkNew(menuItem);
        menuItem.setDate(LocalDate.now());
        menuItem.setDescription(menuItem.getDescription().toLowerCase());
        menuItem.setRestaurant(restaurant);
        MenuItem created = menuItemRepository.save(menuItem);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menu/menuItem/{menuItemId}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/{restaurantId}/menu/menuitem/{menuItemId}")
    public MenuItem getMenuItem (@PathVariable int restaurantId, @PathVariable int menuItemId) {
        log.info("get menuItem {} from actual menu for restaurant {}", menuItemId, restaurantId);
        return menuItemRepository.getActualMenuIteForCurrentRestaurant(restaurantId, menuItemId).get();
    }

    @DeleteMapping("/{restaurantId}/menu/menuitem/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItemForCurrentRestaurant(@PathVariable int restaurantId, @PathVariable int menuItemId) {
        log.info("delete menuItem {} from actual menu for restaurant {}", menuItemId, restaurantId);
        menuItemRepository.deleteActualItemForRestaurant(restaurantId, menuItemId);
    }
}
