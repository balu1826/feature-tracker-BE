package com.talentstream.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.entity.NotificationMessage;
import com.talentstream.service.NotificationMessageService;

@RestController
@RequestMapping("/notifications")
public class NotificationMessageController {

	@Autowired
	private NotificationMessageService service;

	@GetMapping("/getNotifications/{applicantId}")
	public ResponseEntity<?> getApplicantNotifications(@PathVariable Long applicantId) {
		try {
			List<NotificationMessage> notifications = service.getNotifications(applicantId);

			if (notifications.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Their are no notifications to show");
			}

			return ResponseEntity.ok(notifications);

		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong: " + ex.getMessage());
		}
	}

	@DeleteMapping("/{notificationId}/deleteNotification/{applicantId}")
	public ResponseEntity<?> deleteApplicantFromNotification(@PathVariable Long notificationId,
			@PathVariable Long applicantId) {

		try {
			boolean ok = service.removeApplicantFromNotification(notificationId, applicantId);

			if (!ok) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching notification/applicant found.");
			}

			return ResponseEntity.ok("Applicant removed from notification.");

		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}
	
	@DeleteMapping("/deleteAllNotifications/{applicantId}")
    public ResponseEntity<?> removeApplicant(@PathVariable Long applicantId) {
        try {
            service.removeApplicantFromAllNotifications(applicantId);
            return ResponseEntity.ok("Deleted all notifications for the applicant.");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Something went wrong: " + ex.getMessage());
        }
    }
	
	 @PutMapping("/{notificationId}/move-to-seen/{applicantId}")
	    public ResponseEntity<?> moveApplicantToSeen(
	            @PathVariable Long notificationId,
	            @PathVariable Long applicantId) {

	        try {
	            service.moveApplicantToSeen(notificationId, applicantId);
	            return ResponseEntity.ok("Applicant moved to seen list for this notification.");
	        } catch (EntityNotFoundException ex) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	        } 
	        catch(IllegalArgumentException ex) {
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	        }
	        catch (Exception ex) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error: " + ex.getMessage());
	        }
	    }

	    @PutMapping("/move-to-seen-everywhere/{applicantId}")
	    public ResponseEntity<?> moveApplicantToSeenEverywhere(
	            @PathVariable Long applicantId) {

	        try {
	            service.moveApplicantToSeenEverywhere(applicantId);
	            return ResponseEntity.ok("Applicant moved to seen list in all notifications.");
	        } catch (EntityNotFoundException ex) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	        } catch (Exception ex) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error: " + ex.getMessage());
	        }
	    }

}
