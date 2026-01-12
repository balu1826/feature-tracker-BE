// src/main/java/com/talentstream/service/ApplicantService.java
package com.talentstream.service;

import com.talentstream.entity.Applicant;
import com.talentstream.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Autowired
    public ApplicantService(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    public boolean getTourSeen(Long applicantId) {
        return applicantRepository.findById(applicantId)
                .map(Applicant::isTourSeen)
                .orElse(false);
    }

    @Transactional
    public void markTourSeen(Long applicantId) {
        Optional<Applicant> opt = applicantRepository.findById(applicantId);
        if (opt.isPresent()) {
            Applicant a = opt.get();
            if (!a.isTourSeen()) {
                a.setTourSeen(true);
                // no explicit save required if the entity is managed and transaction commits
            }
        }
    }
}
