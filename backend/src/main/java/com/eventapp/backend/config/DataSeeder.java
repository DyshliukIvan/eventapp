package com.eventapp.backend.config;

import com.eventapp.backend.models.Event;
import com.eventapp.backend.repositories.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedEvents(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                eventRepository.save(new Event("Concert", "Prague", "2026-06-12", "Live music event"));
                eventRepository.save(new Event("Festival", "Brno", "2026-06-15", "Open air festival"));
                eventRepository.save(new Event("Conference", "Ostrava", "2026-06-20", "Tech conference"));
                eventRepository.save(new Event("Jazz Night", "Prague", "2026-06-25", "Evening jazz concert"));
            }
        };
    }
}