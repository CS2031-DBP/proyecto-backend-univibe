package com.univibe.registration.web;

import com.univibe.event.model.Event;
import com.univibe.event.repo.EventRepository;
import com.univibe.registration.model.Registration;
import com.univibe.registration.model.RegistrationStatus;
import com.univibe.registration.repo.RegistrationRepository;
import com.univibe.registration.service.QrService;
import com.univibe.user.model.User;
import com.univibe.user.repo.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final QrService qrService = new QrService();

    public RegistrationController(RegistrationRepository registrationRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> register(Authentication auth, @PathVariable Long eventId) {
        String email = (String) auth.getPrincipal();
        User user = userRepository.findByEmail(email).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        registrationRepository.findByUserIdAndEventId(user.getId(), eventId).ifPresent(r -> { throw new IllegalStateException("Already registered"); });

        String payload = qrService.generatePayload(user.getId(), eventId);
        String qrBase64 = qrService.generateBase64Png(payload);

        Registration r = new Registration();
        r.setUser(user);
        r.setEvent(event);
        r.setQrCode(payload);
        registrationRepository.save(r);

        return Map.of("registrationId", r.getId(), "qrBase64", qrBase64);
    }

    @PostMapping("/check-in")
    public Map<String, Object> checkIn(@RequestBody Map<String, @NotNull String> body) {
        String payload = body.get("payload");
        String decoded = new String(java.util.Base64.getUrlDecoder().decode(payload));
        String[] parts = decoded.split(":");
        Long userId = Long.parseLong(parts[0]);
        Long eventId = Long.parseLong(parts[1]);

        Registration r = registrationRepository.findByUserIdAndEventId(userId, eventId).orElseThrow();
        r.setStatus(RegistrationStatus.CHECKED_IN);
        r.setCheckedInAt(Instant.now());
        registrationRepository.save(r);
        return Map.of("status", r.getStatus(), "checkedInAt", r.getCheckedInAt());
    }
}
