package org.darrotech.eventplanner.data;

import org.darrotech.eventplanner.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Chris Bay
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findAll();
    User findByUsername(String username);

}
