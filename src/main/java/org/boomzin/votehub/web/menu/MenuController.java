package org.boomzin.votehub.web.menu;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.MenuItem;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.MenuItemRepository;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@Validated
@RestController
@RequestMapping(value = MenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class MenuController {
    static final String REST_URL = "/api/restaurants/admin";

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    @PostMapping(value = "/{restaurantId}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "menu", allEntries = true)
    public ResponseEntity<List<MenuItem>> createMenuWithLocation(@PathVariable int restaurantId, @Valid @RequestBody Menu menu) {
        log.info("create menu for restaurant {} for today", restaurantId);
        Set<String> descriptions = menu.getList().stream()
                .map(e -> e.getDescription()
                .toLowerCase())
                .collect(Collectors.toSet());
        if (descriptions.size() != menu.getList().size()) {
            throw new IllegalRequestDataException("Menu contains not unique dishes");
        }
        menuItemRepository.deleteAllActualItemsForRestaurant(restaurantId);
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        for (MenuItem menuItem : menu.getList()) {
            checkNew(menuItem);
            menuItem.setDate(LocalDate.now());
            menuItem.setDescription(menuItem.getDescription().toLowerCase());
            menuItem.setRestaurant(restaurant);
            menuItemRepository.save(menuItem);
        }
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menu")
                .buildAndExpand(restaurantId).toUri();
        return ResponseEntity.created(uriOfNewResource).body(menu.getList());
    }

    @Transactional
    @DeleteMapping("/{restaurantId}/menu")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "menu", allEntries = true)
    public void deleteMenuForCurrentRestaurant(@PathVariable int restaurantId) {
        log.info("delete actual menu for restaurant {}", restaurantId);
        menuItemRepository.deleteAllActualItemsForRestaurant(restaurantId);
    }

    @GetMapping("/{restaurantId}/menu")
    public Menu getActualMenuForCurrentRestaurant (@PathVariable int restaurantId) {
        log.info("get actual menu for restaurant {}", restaurantId);
        Menu menu = new Menu();
        menu.setList(menuItemRepository.getActualMenuCurrentRestaurant(restaurantId).get());
        return menu;
    }

    @Transactional
    @PutMapping(value = "/{restaurantId}/menu/menuitem/{menuItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "menu", allEntries = true)
    public void UpdateMenuItem(@PathVariable int restaurantId, @PathVariable int menuItemId,@Valid @RequestBody MenuItem menuItem) {
        log.info("update menu item {} for restaurant {} for today",menuItemId, restaurantId);
        assureIdConsistent(menuItem, menuItemId);
        Optional<Restaurant> restaurant = restaurantRepository.getWithActualMenu(restaurantId);
        if (restaurant.isPresent() && restaurant.get().getMenu().contains(menuItem)) {
            menuItem.setDate(LocalDate.now());
            MenuItem existed = menuItemRepository.getById(menuItemId);
            menuItem.setDescription(existed.getDescription());
            menuItem.setRestaurant(restaurant.get());
            menuItemRepository.save(menuItem);
        } else {
            throw new IllegalRequestDataException("Restaurant menu for today doesn`t contain this dish, use POST to add new");
        }
    }

    @Transactional
    @PostMapping(value = "/{restaurantId}/menu/menuitem", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "menu", allEntries = true)
    public ResponseEntity<MenuItem> createMenuItemWithLocation(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem) {
        log.info("create menu item for restaurant {} for today", restaurantId);
        List<MenuItem> actualMenu = menuItemRepository.getActualMenuCurrentRestaurant(restaurantId).get();
        Set<String> descriptions = actualMenu.stream()
                .map(e -> e.getDescription().toLowerCase())
                .collect(Collectors.toSet());
        descriptions.add(menuItem.getDescription().toLowerCase());
        if (descriptions.size() == actualMenu.size()) {
            throw new IllegalRequestDataException("Menu already contains this item, use method PUT to change price");
        }
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
    @CacheEvict(cacheNames = "menu", allEntries = true)
    public void deleteMenuItemForCurrentRestaurant(@PathVariable int restaurantId, @PathVariable int menuItemId) {
        log.info("delete menuItem {} from actual menu for restaurant {}", menuItemId, restaurantId);
        menuItemRepository.checkBelong(restaurantId, menuItemId);
        menuItemRepository.delete(menuItemId);
    }
}
