package org.boomzin.votehub.web;

import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.model.User;
import org.boomzin.votehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    protected UserRepository repository;

    public ResponseEntity<User> get(int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.findById(id));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        log.info("delete {}", id);
        repository.delete(id);
    }

    public ResponseEntity<User> getWithVotes(int id) {
        log.info("getWithVotes {}", id);
        return ResponseEntity.of(repository.getWithVotes(id));
    }
}