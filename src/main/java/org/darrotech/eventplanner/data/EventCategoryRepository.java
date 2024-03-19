package org.darrotech.eventplanner.data;

import org.darrotech.eventplanner.models.EventCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Chris Bay
 */
@Repository
public interface EventCategoryRepository extends CrudRepository<EventCategory, Integer> {
    List<EventCategory> findByUserId(int userId);//for complete authentication

}
