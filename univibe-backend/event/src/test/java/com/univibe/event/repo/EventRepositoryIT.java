package com.univibe.event.repo;

import com.univibe.event.model.Event;
import com.univibe.event.model.EventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
class EventRepositoryIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    EventRepository eventRepository;

    @Test
    void shouldFindByStatusWhenSaved() {
        Event e = new Event();
        e.setTitle("Test");
        e.setCategory("workshop");
        e.setDescription("desc");
        e.setStatus(EventStatus.PENDING);
        e.setStartTime(Instant.now());
        e.setEndTime(Instant.now().plusSeconds(3600));
        eventRepository.save(e);

        List<Event> pending = eventRepository.findByStatus(EventStatus.PENDING);
        assertThat(pending).isNotEmpty();
    }
}
