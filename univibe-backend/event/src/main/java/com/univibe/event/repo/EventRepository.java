package com.univibe.event.repo;

import com.univibe.event.model.Event;
import com.univibe.event.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByCategory(String category);
}
