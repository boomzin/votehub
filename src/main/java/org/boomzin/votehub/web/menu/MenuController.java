package org.boomzin.votehub.web.menu;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.MenuItem;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.MenuItemRepository;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.to.Menu;
import org.boomzin.votehub.to.MenuTo;
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
    static final String REST_URL = "/api/admin/menus";

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @GetMapping()
    public List<MenuTo> getAllActualMenus() {
        log.info("get actual menus for all restaurants");
        return menuItemRepository.getMenusOnDate(LocalDate.now());
    }

    @GetMapping("/{restaurantId}")
    public List<MenuItem> getActualMenu(@PathVariable int restaurantId) {
        log.info("get actual menu for restaurant {}", restaurantId);
        return menuItemRepository.getMenuCurrentRestaurantOnDate(restaurantId, LocalDate.now());
    }

    @Transactional
    @PostMapping(value = "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "restaurants", allEntries = true)
    public ResponseEntity<List<MenuItem>> createMenuWithLocation(@PathVariable int restaurantId, @Valid @RequestBody Menu menu) {
        log.info("create menu for restaurant {} for today", restaurantId);
        isRestaurantExist(restaurantId);
        checkUniqueItems(menu);
        if (menuItemRepository.getMenuCurrentRestaurantOnDate(restaurantId, LocalDate.now()).size() > 0) {
            throw new IllegalRequestDataException("The restaurant "
                    + restaurantId
                    + " already has menu for today. Choose PUT method for changing");
        }
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        for (MenuItem menuItem : menu.getList()) {
            checkNew(menuItem);
            menuItem.setDate(LocalDate.now());
            menuItem.setRestaurant(restaurant);
            menuItemRepository.save(menuItem);
        }
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + restaurantId)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(menu.getList());
    }

    @Transactional
    @PutMapping(value = "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void updateMenu(@PathVariable int restaurantId, @Valid @RequestBody Menu menu) {
        log.info("update menu for restaurant {} for today", restaurantId);
        isRestaurantExist(restaurantId);
        checkUniqueItems(menu);
        menuItemRepository.deleteAllMenuItemsForRestaurantOnDate(restaurantId, LocalDate.now());
        for (MenuItem menuItem : menu.getList()) {
            menuItem.setId(null);
            menuItem.setDate(LocalDate.now());
            menuItem.setRestaurant(restaurantRepository.getById(restaurantId));
            menuItemRepository.save(menuItem);
        }
    }

    private void checkUniqueItems(@RequestBody @Valid Menu menu) {
        Set<String> descriptions = menu.getList().stream()
                .map(e -> e.getDescription().toLowerCase())
                .collect(Collectors.toSet());
        if (descriptions.size() != menu.getList().size()) {
            throw new IllegalRequestDataException("Menu contains not unique dishes");
        }
    }

    @Transactional
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteActualMenu(@PathVariable int restaurantId) {
        log.info("delete actual menu for restaurant {}", restaurantId);
        menuItemRepository.deleteAllMenuItemsForRestaurantOnDate(restaurantId, LocalDate.now());
    }


    @Transactional
    @PutMapping(value = "/{restaurantId}/menuitem/{menuItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void updateMenuItem(@PathVariable int restaurantId, @PathVariable int menuItemId, @Valid @RequestBody MenuItem menuItem) {
        log.info("update menu item {} for restaurant {} for today", menuItemId, restaurantId);
        assureIdConsistent(menuItem, menuItemId);
        Optional<Restaurant> restaurant = restaurantRepository.getWithMenuOnDate(restaurantId, LocalDate.now());
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
    @PostMapping(value = "/{restaurantId}/menuitem", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "restaurants", allEntries = true)
    public ResponseEntity<MenuItem> createMenuItemWithLocation(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem) {
        log.info("create menu item for restaurant {} for today", restaurantId);
        if (menuItemRepository.getMenuCurrentRestaurantOnDate(restaurantId, LocalDate.now()).size() > 0) {
            List<MenuItem> actualMenu = menuItemRepository.getMenuCurrentRestaurantOnDate(restaurantId, LocalDate.now());
            Set<String> descriptions = actualMenu.stream()
                    .map(e -> e.getDescription().toLowerCase())
                    .collect(Collectors.toSet());
            descriptions.add(menuItem.getDescription().toLowerCase());
            if (descriptions.size() == actualMenu.size()) {
                throw new IllegalRequestDataException("Menu already contains this item, use method PUT to change price");
            }
        }
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        checkNew(menuItem);
        menuItem.setDate(LocalDate.now());
        menuItem.setDescription(menuItem.getDescription());
        menuItem.setRestaurant(restaurant);
        MenuItem created = menuItemRepository.save(menuItem);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menuItem/{menuItemId}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }


    @GetMapping("/{restaurantId}/menuitem/{menuItemId}")
    public MenuItem getMenuItem(@PathVariable int restaurantId, @PathVariable int menuItemId) {
        log.info("get menuItem {} from actual menu for restaurant {}", menuItemId, restaurantId);
        return menuItemRepository.getMenuItemsForCurrentRestaurantOnDate(restaurantId, menuItemId, LocalDate.now()).get();
    }

    @DeleteMapping("/{restaurantId}/menuitem/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteMenuItem(@PathVariable int restaurantId, @PathVariable int menuItemId) {
        log.info("delete menuItem {} from actual menu for restaurant {}", menuItemId, restaurantId);
        checkBelong(restaurantId, menuItemId);
        menuItemRepository.delete(menuItemId);
    }

    void isRestaurantExist(int restaurantId){
        if (!restaurantRepository.findById(restaurantId).isPresent()){
            throw new IllegalRequestDataException("The restaraunt with id = "
                    + restaurantId
                    + " does not exist");
        }
    }

     void checkBelong(int restaurantId, int menuItemId) {
        menuItemRepository.getMenuItemsForCurrentRestaurantOnDate(restaurantId, menuItemId, LocalDate.now()).orElseThrow(
                () -> new IllegalRequestDataException("MenuItem " + menuItemId + " not found in actual menu for restaurant" + restaurantId));
    }
}
