package com.univibe.notification.web;

import com.univibe.notification.model.Notification;
import com.univibe.notification.repo.NotificationRepository;
import com.univibe.user.model.User;
import com.univibe.user.repo.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/{userId}")
    public List<Notification> list(@PathVariable Long userId, Authentication auth) {
        var requester = userRepository.findByEmail((String) auth.getPrincipal()).orElseThrow();
        if (!requester.getId().equals(userId) && (requester.getRole() == null || requester.getRole() != com.univibe.user.model.Role.ADMIN)) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','SERVER')")
    public Notification send(@PathVariable Long userId, @RequestParam String title, @RequestParam String message) {
        User recipient = userRepository.findById(userId).orElseThrow();
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setTitle(title);
        n.setMessage(message);
        notificationRepository.save(n);
        messagingTemplate.convertAndSend("/queue/notifications." + userId, n);
        return n;
    }
}
