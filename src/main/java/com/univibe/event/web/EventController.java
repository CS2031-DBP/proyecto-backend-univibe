package com.univibe.event.web;

import com.univibe.event.model.Event;
import com.univibe.event.repo.EventRepository;
import com.univibe.event.model.EventStatus;
import com.univibe.event.dto.EventCreateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> list() {
        return eventRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SERVER')")
    public Event create(@RequestBody EventCreateRequest req) {
        Event event = new Event();
        event.setTitle(req.getTitle());
        event.setCategory(req.getCategory());
        event.setDescription(req.getDescription());
        event.setFaculty(req.getFaculty());
        event.setCareer(req.getCareer());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        return eventRepository.save(event);
    }

    @PostMapping("/{eventId}/start")
    @PreAuthorize("hasAnyRole('ADMIN','SERVER')")
    public Event start(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setStatus(EventStatus.LIVE);
        return eventRepository.save(event);
    }

    @PostMapping("/{eventId}/finish")
    @PreAuthorize("hasAnyRole('ADMIN','SERVER')")
    public Event finish(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setStatus(EventStatus.FINISHED);
        return eventRepository.save(event);
    }
}
