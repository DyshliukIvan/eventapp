package com.eventapp.backend.repositories;

import com.eventapp.backend.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByLocationIgnoreCase(String location);
}