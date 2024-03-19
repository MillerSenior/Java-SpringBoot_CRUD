package org.darrotech.eventplanner.data;

import org.darrotech.eventplanner.models.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository <Event, Integer>{
    List<Event> findByUserId(int userId);//for complete authentication
}
