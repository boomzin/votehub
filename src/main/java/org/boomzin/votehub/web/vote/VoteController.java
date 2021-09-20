package org.boomzin.votehub.web.vote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Vote;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.boomzin.votehub.repository.VoteRepository;
import org.boomzin.votehub.to.VoteTo;
import org.boomzin.votehub.web.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        return voteRepository.getAll(authUser.getUser().getId());
    }

    @GetMapping("/today")
    public VoteTo getByToday (@AuthenticationPrincipal AuthUser authUser) {
        log.info("get vote for user {} fo today", authUser.getUser().getId());
        return voteRepository.getByDate(authUser.getUser().getId(), LocalDate.now())
                .orElseThrow(() ->new IllegalRequestDataException("You did not vote today"));
    }

    @GetMapping("/{id}")
    public VoteTo get (@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("get vote {} for user {}", id, authUser.getUser().getId());
        checkBelongTodayVoting(id, authUser.getUser().getId());
        return voteRepository.get(id, authUser.getUser().getId()).get();
    }

    @Transactional
    @PostMapping()
    public ResponseEntity<Vote> createWithLocation(@RequestParam int restaurantId, @AuthenticationPrincipal AuthUser authUser) {
        log.info("user {} is voting for restaurant {}", authUser.getUser().getId(), restaurantId);
        Optional<VoteTo> existedVote = voteRepository.getByDate(authUser.getUser().getId(), LocalDate.now());
        if (existedVote.isPresent()) {
            throw new IllegalRequestDataException("You already have been voted today, voteId is "
                    + existedVote.get().getVote().id()
                    + ", choose PUT method for change your mind");
        }
        checkRestaurantHasMenu(restaurantId);
        Vote vote = new Vote(LocalDate.now(), authUser.getUser(), restaurantRepository.getById(restaurantId));
        Vote created = voteRepository.save(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId, @PathVariable int id) {
        int userId = authUser.id();
        log.info("update vote {} for user {}", id, userId);
        checkRestaurantHasMenu(restaurantId);
        checkBelongTodayVoting(id, userId);
        Vote vote = voteRepository.getById(id);
        vote.setRestaurant(restaurantRepository.findById(restaurantId).get());
        voteRepository.save(vote);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        int userId = authUser.id();
        log.info("delete vote {} for user {}", id, userId);
        checkBelongTodayVoting(id, userId);
        voteRepository.delete(id);
    }

    private void checkRestaurantHasMenu(int restaurantId) {
        if (!restaurantRepository.getWithMenuOnDate(restaurantId, LocalDate.now()).isPresent() ) {
            throw new IllegalRequestDataException("The selected restaurant "
                    + restaurantId
                    + " does not have a menu for today, choose another one");
        }
    }

    void checkBelongTodayVoting(int id, int userId) {
        voteRepository.isExistToday(id, userId).orElseThrow(
                () -> new IllegalRequestDataException("You can handle votes only for today" +
                        ", the vote " + id + " is not belong today's voting result for user" + userId));
    }

}
