package org.boomzin.votehub.web.restaurant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@Validated
@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = {"menu", "restaurants"})
public class AdminRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";

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

    @DeleteMapping("/{id}")
    @CacheEvict(cacheNames = {"menu", "restaurants"}, allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        restaurantRepository.deleteExisted(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant.getName());
        checkNew(restaurant);
        restaurant.setName(restaurant.getName());
        restaurant.setAddress(restaurant.getAddress());
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update restaurant with id={}", id);
        assureIdConsistent(restaurant, id);
        restaurantRepository.save(restaurant);
    }

    @GetMapping("/{id}/with-votes")
    public Restaurant getWithVotes(@PathVariable int id) {
        log.info("get restaurant {} with votes", id);
        return restaurantRepository.getWithVotes(id)
                .orElseThrow(() ->new IllegalRequestDataException("The restaurant"
                        + id
                        + "does not have any votes"));
    }

    @GetMapping("/{id}/with-votes-actual")
    public Restaurant getWithActualVotes(@PathVariable int id) {
        log.info("get restaurant {} with votes for today", id);
        return restaurantRepository.getWithVotesOnDate(id, LocalDate.now())
                .orElseThrow(() ->new IllegalRequestDataException("The restaurant"
                        + id
                        + "does not have votes today"));
    }

    @GetMapping("/{id}/with-votes-on-date")
    public Restaurant getWithVotesOnDate (@PathVariable int id, LocalDate date) {
        log.info("get restaurant {} with votes on date {}", id, date);
        return restaurantRepository.getWithVotesOnDate(id, date)
                .orElseThrow(() ->new IllegalRequestDataException("The restaurant"
                        + id
                        + "does not have votes on this date"));
    }
    @GetMapping("/with-votes-actual")
    public List<Restaurant> getAllWithActualVotes() {
        log.info("get restaurants with votes for today");
        return restaurantRepository.getAllWithVotesOnDate(LocalDate.now());
    }

    @GetMapping("/with-votes-on-date")
    public List<Restaurant> getAllWithVotesOnDate (@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate date) {
        log.info("get restaurants with votes on date {}", date);
        return restaurantRepository.getAllWithVotesOnDate(date);
    }
}
