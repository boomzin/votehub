package org.boomzin.votehub.web.restaurant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.to.RestaurantWithRating;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = {"menu", "restaurants"})
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository restaurantRepository;

    private UniqueNameAddressValidator nameAddressValidator;


    @GetMapping
    @Cacheable(cacheNames = "restaurants")
    public List<Restaurant> getAll() {
        log.info("getAll");
        return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable int id) {
        log.info("get id {}", id);
        return ResponseEntity.of(restaurantRepository.findById(id));
    }

    @GetMapping("/{id}/with-menus")
    @Cacheable(cacheNames = "menu")
    public ResponseEntity<Restaurant> getWithMenus(@PathVariable int id) {
        log.info("get id {} with menus", id);
        return ResponseEntity.of(restaurantRepository.getWithMenus(id));
    }

    @GetMapping("/{id}/with-menu-on-date")
    public ResponseEntity<Restaurant> getWithMenuOnDate(@PathVariable int id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get id {} with menu on date {}", id, date);
        return ResponseEntity.of(restaurantRepository.getWithMenuOnDate(id, date));
    }

    @GetMapping("/{id}/with-actual-menu")
    @Cacheable(cacheNames = "menu")
    public ResponseEntity<Restaurant> getWithActualMenu(@PathVariable int id) {
        log.info("get id {} with menu on today", id);
        return ResponseEntity.of(restaurantRepository.getWithMenuOnDate(id, LocalDate.now()));
    }

    @GetMapping("/with-actual-menu")
    @Cacheable(cacheNames = "menu")
    public ResponseEntity<List<Restaurant>> getAllWithActualMenu() {
        log.info("getAll with menu on today");
        return ResponseEntity.ok(restaurantRepository.getAllWithMenuOnDate(LocalDate.now()));
    }

    @GetMapping("/with-menu-on-date")
    public ResponseEntity<List<Restaurant>> getAllWithMenuOnDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("getAll with menu on date {}", date);
        return ResponseEntity.ok(restaurantRepository.getAllWithMenuOnDate(date));
    }

    @GetMapping("/rating-on-date")
    public List<RestaurantWithRating> getRatingOnDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get rating on date {}", date);
        return restaurantRepository.getRatingOnDate(date);
    }

    @GetMapping("/actual-rating")
    public List<RestaurantWithRating> getRatingOnDate() {
        log.info("get rating for today");
        return restaurantRepository.getRatingOnDate(LocalDate.now());
    }
}
