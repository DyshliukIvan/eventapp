package com.eventapp.backend.controllers;

import com.eventapp.backend.models.Event;
import com.eventapp.backend.repositories.EventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> getEvents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location
    ) {
        if (query != null && !query.isBlank()) {
            return eventRepository.findByTitleContainingIgnoreCase(query);
        }

        if (location != null && !location.isBlank()) {
            return eventRepository.findByLocationIgnoreCase(location);
        }

        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Event not found"));
    }
}