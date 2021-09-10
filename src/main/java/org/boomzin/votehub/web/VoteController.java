package org.boomzin.votehub.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Vote;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.repository.VoteRepository;
import org.boomzin.votehub.to.VoteTo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

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
        return voteRepository.getById(authUser.getUser().getId(), id).get();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vote> createWithLocation(@RequestBody Vote vote, @AuthenticationPrincipal AuthUser authUser) {
        log.info("user {} is voting for restaurant {}", authUser.getUser().getId(), vote.getRestaurant().getId());
        checkNew(vote);
        if (voteRepository.getActual(authUser.getUser().getId()).isPresent()) {
            throw new IllegalRequestDataException(voteRepository.getClass().getSimpleName() + "you already have been voted today, voteId is "
                    + voteRepository.getActual(authUser.getUser().getId()).get().id()
                    + ", choose DELETE or PUT method for change your mind");
        }
        if (restaurantRepository.getWithActualMenu(vote.getRestaurant().getId()).isEmpty() ) {
            throw new IllegalRequestDataException(voteRepository.getClass().getSimpleName() + "the selected restaurant "
                    + vote.getRestaurant().getId()
                    + " does not have a menu for today, choose another");
        }
        vote.setName(vote.getName().toLowerCase());
        vote.setDate(LocalDate.now());
        vote.setUser(authUser.getUser());
        vote.setRestaurant(restaurantRepository.findById(vote.getRestaurant().getId()).get());
        Vote created = voteRepository.save(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
//    @PostMapping(value = "/restaurants/{restaurantId}/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Vote> createWithLocation(@PathVariable int restaurantId, @RequestBody Vote vote, @AuthenticationPrincipal AuthUser authUser) {
//        log.info("user {} is voting for restaurant {}", authUser.getUser().getId(), restaurantId);
//        checkNew(vote);
//        if (voteRepository.getActual(authUser.getUser().getId()).isPresent()) {
//            throw new IllegalRequestDataException(voteRepository.getClass().getSimpleName() + "you already have been voted today, voteId is "
//                    + voteRepository.getActual(authUser.getUser().getId()).get().id()
//                    + ", choose DELETE or PUT method for change your mind");
//        }
//        if (restaurantRepository.getWithActualMenu(restaurantId).isEmpty() ) {
//            throw new IllegalRequestDataException(voteRepository.getClass().getSimpleName() + "the selected restaurant "
//                    + vote.getRestaurant().getId()
//                    + " does not have a menu for today, choose another");
//        }
//        vote.setName(vote.getName().toLowerCase());
//        vote.setDate(LocalDate.now());
//        vote.setUser(authUser.getUser());
//        vote.setRestaurant(restaurantRepository.findById(restaurantId).get());
//        Vote created = voteRepository.save(vote);
//        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path(REST_URL + "/votes/{id}")
//                .buildAndExpand(created.getId()).toUri();
//        return ResponseEntity.created(uriOfNewResource).body(created);
//    }

//    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(@RequestBody Vote vote) {
//    }

}
