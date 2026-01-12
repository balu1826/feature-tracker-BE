package com.talentstream.service;

import com.talentstream.entity.Hackathon;
import com.talentstream.entity.HackathonStatus;
import com.talentstream.entity.HackathonRegister;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.HackathonRepository;
import com.talentstream.repository.HackathonRegisterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HackathonRegisterService {
	private final HackathonRegisterRepository repo;
	@Autowired
	private ApplicantProfileRepository appProRepo;
	
	public HackathonRegisterService(HackathonRegisterRepository repo) {
		this.repo = repo;
	}
	
	 @Autowired
	    public ApplicantRepository appRepo;
	 
	 @Autowired
	    public HackathonRepository hackRepo;

	public HackathonRegister findByHackathonAndUser(Long hackathonId, Long userId) {
		 if (!appRepo.existsById(userId)) {
	            throw new IllegalArgumentException("Applicant not found with id: " + userId);
	        }
		 if (!hackRepo.existsById(hackathonId)) {
	            throw new IllegalArgumentException("Hackathon not found with id: " + hackathonId);
	        }
		
		 return repo.findByHackathonIdAndUserId(hackathonId, userId)
		            .orElseThrow(() -> new IllegalArgumentException(
		                "Applicant with id " + userId + " did not register for the hackathon with id " + hackathonId
		            ));
	}

	 public HackathonRegister register(Long hackathonId, Long userId) {
	        if (!appRepo.existsById(userId)) {
	            throw new IllegalArgumentException("Applicant not found with id: " + userId);
	        }
	        Hackathon hackathon = hackRepo.findById(hackathonId)
	                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found with id: " + hackathonId));

	        if (hackathon.getStatus() == HackathonStatus.COMPLETED) {
	            throw new IllegalStateException("Hackathon is already completed. Cannot register.");
	        }

	        Optional<HackathonRegister> existing = repo.findByHackathonIdAndUserId(hackathonId, userId);
	        if (existing.isPresent()) {
	            throw new IllegalStateException(
	                "User already registered for hackathon with id: " + existing.get().getId()
	            );
	        }
	        String firstName = appProRepo.findByApplicantId(userId).getBasicDetails().getFirstName();
	        String lastName = appProRepo.findByApplicantId(userId).getBasicDetails().getLastName();

	        HackathonRegister r = new HackathonRegister();
	        r.setHackathonId(hackathonId);
	        r.setUserId(userId);
	        r.setRegistaratinStatus(true);
	        r.setSubmitStatus(false);
	        r.setRegisteredAt(LocalDateTime.now().plus(Duration.ofMinutes(330)));
	        r.setName(firstName + " " + lastName);
	        hackathon.setRegistrationCount(hackathon.getRegistrationCount() + 1);
	        hackRepo.save(hackathon);
	        
	        
	        return repo.save(r);
	    }

	public List<HackathonRegister> listByHackathon(Long hackathonId) {
		return repo.findByHackathonId(hackathonId);
	}
	
	public List<HackathonRegister> listByApplicant(Long applicantId) {
		return repo.findByUserId(applicantId);
	}
	
	public Long getRegistrationCountByUser(Long userId) {
		if (!appRepo.existsById(userId)) {
			 throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found with id: " + userId);
		    }
		    try {
		        return repo.countByUserId(userId);
		    } catch (Exception e) {
		        throw new RuntimeException("Error fetching register count for userid: " + userId, e);
		    }
		}

	
}
