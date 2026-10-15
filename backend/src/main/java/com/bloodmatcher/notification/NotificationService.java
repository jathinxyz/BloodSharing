package com.bloodmatcher.notification;

import com.bloodmatcher.common.NotificationType;
import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.notification.dto.NotificationDto;
import com.bloodmatcher.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailNotificationService emailNotificationService) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public Notification notifyUser(User user, NotificationType type, String title, String message, Long requestId, boolean attemptEmail) {
        String deliveryStatus = "IN_APP_SAVED";

        if (attemptEmail && user.getEmail() != null) {
            boolean sent = emailNotificationService.sendEmail(user.getEmail(), title, message);
            deliveryStatus = sent ? "EMAIL_SENT" : "EMAIL_FAILED";
        }

        Notification notification = new Notification(user, type, title, message, requestId, deliveryStatus);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationDto::from);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        return NotificationDto.from(notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadForUser(userId);
    }
}
