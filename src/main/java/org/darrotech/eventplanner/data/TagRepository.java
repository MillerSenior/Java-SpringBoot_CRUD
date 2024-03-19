package org.darrotech.eventplanner.data;

import org.darrotech.eventplanner.models.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Chris Bay
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    List<Tag> findByUserId(int userId);//for complete authentication
    List<Tag> findByEventsId(int eventId);
}
