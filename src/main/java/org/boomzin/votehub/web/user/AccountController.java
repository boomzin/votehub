package org.boomzin.votehub.web.user;

import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.model.Role;
import org.boomzin.votehub.model.User;
import org.boomzin.votehub.web.AuthUser;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.EnumSet;

import static org.boomzin.votehub.config.WebSecurityConfig.PASSWORD_ENCODER;
import static org.boomzin.votehub.util.ValidationUtil.assureIdConsistent;
import static org.boomzin.votehub.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AccountController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "users")
public class AccountController extends AbstractUserController {
    static final String REST_URL = "/api/account";

    @GetMapping
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get account {}", authUser.id());
        return authUser.getUser();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "users", key = "#authUser.username")
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        super.delete(authUser.id());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@RequestBody User user) {
        log.info("register {}", user);
        checkNew(user);
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
        user.setRoles(EnumSet.of(Role.USER));
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CachePut(value = "users", key = "#authUser.username")
    public void update(@RequestBody User user, @AuthenticationPrincipal AuthUser authUser) {
        assureIdConsistent(user, authUser.id());
        User updated = authUser.getUser();
        updated.setUsername(user.getUsername());
        updated.setEmail(user.getEmail().toLowerCase());
        updated.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
        repository.save(updated);
    }

    @GetMapping("/with-votes")
    public ResponseEntity<User> getWithVotes(@AuthenticationPrincipal AuthUser authUser) {
        return super.getWithVotes(authUser.id());
    }
}