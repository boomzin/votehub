package org.boomzin.votehub.web.vote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Vote;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.repository.VoteRepository;
import org.boomzin.votehub.to.VoteTo;
import org.boomzin.votehub.web.AuthUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class VoteController {
    static final String REST_URL = "/api/votes";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;

    @GetMapping()
    public List<VoteTo> getAll (@AuthenticationPrincipal AuthUser authUser) {
        log.info("get vote for user {}", authUser.getUser().getId());
        return voteRepository.getAll(authUser.getUser().getId()).get();
    }

    @GetMapping("/{id}")
    public VoteTo get (@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("get vote {} for user {}", id, authUser.getUser().getId());
        return voteRepository.get(id, authUser.getUser().getId()).get();
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "restaurants")
    public ResponseEntity<Vote> createWithLocation(@Valid @RequestBody Vote vote, @AuthenticationPrincipal AuthUser authUser) {
        log.info("user {} is voting for restaurant {}", authUser.getUser().getId(), vote.getRestaurant().getId());
        checkNew(vote);
        Optional<Vote> existedVote = voteRepository.getActual(authUser.getUser().getId());
        if (existedVote.isPresent()) {
            throw new IllegalRequestDataException("You already have been voted today, voteId is "
                    + existedVote.get().id()
                    + ", choose DELETE or PUT method for change your mind");
        }
        checkRestaurantHasMenu(vote);
        vote.setDate(LocalDate.now());
        vote.setUser(authUser.getUser());
        vote.setRestaurant(restaurantRepository.findById(vote.getRestaurant().getId()).get());
        Vote created = voteRepository.save(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    private void checkRestaurantHasMenu(Vote vote) {
        if (!restaurantRepository.getWithActualMenu(vote.getRestaurant().getId()).isPresent() ) {
            throw new IllegalRequestDataException("The selected restaurant "
                    + vote.getRestaurant().getId()
                    + " does not have a menu for today, choose another one");
        }
    }

    @Transactional
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants")
    public void update(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody Vote vote, @PathVariable int id) {
        int userId = authUser.id();
        log.info("update {} for user {}", vote, userId);
        assureIdConsistent(vote, id);
        checkRestaurantHasMenu(vote);
        voteRepository.checkBelong(id, userId);
        vote.setDate(LocalDate.now());
        vote.setUser(authUser.getUser());
        vote.setRestaurant(restaurantRepository.findById(vote.getRestaurant().getId()).get());
        voteRepository.save(vote);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants")
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        int userId = authUser.id();
        log.info("delete vote {} for user {}", id, userId);
        voteRepository.checkBelong(id, userId);
        voteRepository.delete(id);
    }
}
