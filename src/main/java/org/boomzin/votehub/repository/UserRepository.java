package org.boomzin.votehub.repository;

import org.boomzin.votehub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}