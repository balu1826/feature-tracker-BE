package com.talentstream.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.talentstream.entity.Hackathon;
import com.talentstream.entity.HackathonStatus;
import com.talentstream.repository.HackathonRepository;

import java.time.LocalDate;
import java.util.List;

@Component
public class HackathonStatusScheduler {

    private final HackathonRepository hackathonRepository;

    public HackathonStatusScheduler(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateHackathonStatuses() {
    	LocalDate now = LocalDate.now();
    	List<Hackathon> hackathons = hackathonRepository.findAll();


    	 for (Hackathon h : hackathons) {
             if (h.getStartAt() != null && h.getEndAt() != null) {
                 if (now.isBefore(h.getStartAt())) {
                     h.setStatus(HackathonStatus.UPCOMING);
                 } else if ((now.isEqual(h.getStartAt()) || now.isAfter(h.getStartAt())) && now.isBefore(h.getEndAt())) {
                     h.setStatus(HackathonStatus.ACTIVE);
                 } else if (now.isAfter(h.getEndAt())) {
                     h.setStatus(HackathonStatus.COMPLETED);
                 }
             }
         }

        hackathonRepository.saveAll(hackathons);
    }
}
