package org.boomzin.votehub.web.restaurant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.to.RestaurantWithRating;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = {"menu", "restaurants"})
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository restaurantRepository;

    private UniqueNameAddressValidator nameAddressValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(nameAddressValidator);
    }

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
        return ResponseEntity.of(restaurantRepository.getWithMenusOnDate(id, date));
    }

    @GetMapping("/{id}/with-actual-menu")
    @Cacheable(cacheNames = "menu")
    public ResponseEntity<Restaurant> getWithActualMenu(@PathVariable int id) {
        log.info("get id {} with menu on today", id);
        return ResponseEntity.of(restaurantRepository.getWithActualMenu(id));
    }

    @GetMapping("/with-actual-menu")
    @Cacheable(cacheNames = "menu")
    public ResponseEntity<List<Restaurant>> getAllWithActualMenu() {
        log.info("getAll with menu on today");
        return ResponseEntity.of(restaurantRepository.getAllWithActualMenu());
    }

    @GetMapping("/with-menu-on-date")
    public ResponseEntity<List<Restaurant>> getAllWithMenuOnDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("getAll with menu on date {}", date);
        return ResponseEntity.of(restaurantRepository.getAllWithMenuOnDate(date));
    }

    @DeleteMapping("/admin/{id}")
    @CacheEvict(cacheNames = {"menu", "restaurants"}, allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        restaurantRepository.deleteExisted(id);
    }

    @PostMapping(value = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        restaurant.setName(restaurant.getName().toLowerCase());
        restaurant.setAddress(restaurant.getAddress().toLowerCase());
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(value = "/admin/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        restaurant.setName(restaurant.getAddress().toLowerCase());
        restaurant.setAddress(restaurant.getAddress().toLowerCase());
        restaurant.setMenu(null);
        restaurant.setVotes(null);
        restaurantRepository.save(restaurant);
    }

    @GetMapping("/rating-on-date")
    public List<RestaurantWithRating> getRatingOnDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get rating on date {}", date);
        return restaurantRepository.getRatingOnDate(date).get();
    }

    @GetMapping("/actual-rating")
    @Cacheable(cacheNames = "restaurants")
    public List<RestaurantWithRating> getRatingOnDate() {
        log.info("get rating for today");
        return restaurantRepository.getActualRating().get();
    }

    @GetMapping("/admin/{id}/with-votes")
    public Restaurant getWithVotes(@PathVariable int id) {
        log.info("get restaurant {} with votes", id);
        return restaurantRepository.getWithVotes(id).get();
    }

    @GetMapping("/admin/{id}/with-votes-actual")
    public Restaurant getWithActualVotes(@PathVariable int id) {
        log.info("get restaurant {} with votes for today", id);
        return restaurantRepository.getWithActualVotes(id).get();
    }

    @GetMapping("/admin/{id}/with-votes-on-date")
    public Restaurant getWithVotesOnDate (@PathVariable int id, LocalDate date) {
        log.info("get restaurant {} with votes on date {}", id, date);
        return restaurantRepository.getWithVotesOnDate(id, date).get();
    }
    @GetMapping("/admin/with-votes-actual")
    public List<Restaurant> getAllWithActualVotes() {
        log.info("get restaurants with votes for today");
        return restaurantRepository.getAllWithActualVotes().get();
    }

    @GetMapping("/admin/with-votes-on-date")
    public List<Restaurant> getWithVotesOnDate (LocalDate date) {
        log.info("get restaurants with votes on date {}", date);
        return restaurantRepository.getAllWithVotesOnDate(date).get();
    }
}
