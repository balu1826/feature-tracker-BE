package com.talentstream.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.talentstream.filter.JwtRequestFilter;

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	// @Autowired
	private UserDetailsService myUserDetailsService;
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	// @Autowired

	// @Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
	}

	// @Bean

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().authorizeRequests().antMatchers(
				"/job/recruiters/cloneJob/{jobId}/{jobRecruiterId}", "/viewjob/recruiter/viewjob/{jobId}",
				"/applicant-image/getphoto1/{applicantId}",
				"/applyjob/recruiter/{jobRecruiterId}/appliedapplicants/{id}", "/recuriters/job-alerts/{recruiterId}",
				"/recuriters/appledjobs/{recruiterId}/unread-alert-count",
				"/applicantprofile/{applicantId}/profile-view1", "/companyprofile/approval-status/{jobRecruiterId}",
				"/applyjob/applied-applicant/search", "/applyjob/recruiter/{jobRecruiterId}/{status}",
				"/applyjob/appliedapplicants/{jobId}", "/job/recruiters/search", "/job/recruiterssearchBySkillName",
				"/applyjob/recruiter/{jobRecruiterId}/appliedapplicants",
				"/applyjob/recruiters/scheduleInterview/{applyJobId}",
				"/applyjob/recruiters/applyjob-update-status/{applyJobId}/{newStatus}",
				"/applyjob/recruiter/{recruiterId}/interviews/{status}",
				"/applyjob/recruiters/applyjobapplicantscount/{recruiterId}", "/applyjob/recruiters/selected/count",
				"/applyjob/recruiters/countShortlistedAndInterviewed", "/applyjob/current-date",
				"/companyprofile/recruiters/company-profiles/{jobRecruiterId}",
				"/companyprofile/recruiters/getCompanyProfile/{id}", "/job/recruiters/saveJob/{jobRecruiterId}",
				"/job/recruiters/viewJobs/{jobRecruiterId}", "/recuriters/viewRecruiters",
				"/job/recruiterssearchByJobTitle", "/job/recruiterssearchByLocation",
				"/job/recruiterssearchByIndustryType", "/job/recruiterssearchByEmployeeType",
				"/job/searchByMinimumQualification", "/job/recruiterssearchBySpecialization",
				"/job/recruiterssearchBySkillNameAndExperience", "/team/{recruiterId}/team-members",
				"/team/teammembers/{recruiterId}", "/team/{teamMemberId}", "/team/{teamMemberId}/reset-password",
				"/job/recruiterscountjobs/{recruiterId}", "/videos/updateThumbnail/{videoId}", "/videos/uploadVideo",
				"/team/{teamMemberId}/reset-password", "/job/recruiterscountjobs/{recruiterId}", "/blogs/inactive",
				"/blogs/all", "/blogs/updateOrDelete", "/blogs/getBlogById/{id}",
				"/recruiter/hackathons/winsCount/{winnerId}", "/recruiter/hackathons/registerCount/{userId}",
				"/recruiter/hackathons/getAllCreadtedHackathons/{recruiterId}", "/recruiter/hackathons/createHackathon",
				"/hackathons/{hackathonId}/getAllHackathonRegistrations", "/recruiter/hackathons/{hackathonId}/declare-winner/{winnerId}",
				"/api/mentor-connect/recruiter/createMentorConnect","/api/feedback-forms/recruiter/{recruiterId}/**")
		.hasAnyRole("JOBRECRUITER")
				.antMatchers("/jobVisit/applicant/track-visit", "/skill-badges/{id}/skill-badges", "/skill-badges/save",
						"/savedjob/applicants/deletejob/{applicantId}/{jobId}", "/applicant/{id}/profilestatus",
						"/applicantprofile/{applicantId}/profile-view", "/applicantprofile/updateprofile/{applicantid}",
						"/viewjob/applicant/viewjob/{jobId}/{applicantId}",
						"/applicantprofile/createprofile/{applicantid}", "/applicantprofile/getdetails/{applicantid}",
						"/applyjob/applicants/applyjob/{applicantId}/{jobId}", "/applyjob/getAppliedJobs/{applicantId}",
						"/applyjob/getScheduleInterviews/applicant/{applicantId}/{applyJobId}",
						"/recommendedjob/findrecommendedjob/{applicantId}", "/appicant/viewApplicants",
						"/savedjob/applicants/savejob/{applicantId}/{jobId}", "/savedjob/getSavedJobs/{applicantId}",
						"/searchjob/applicant/searchjobbyskillname/{applicantId}/jobs/{skillName}",
						"/viewjob/applicant/viewjob/{jobId}", "/applicant-pdf/getresume/{applicantId}",
						"/applicant-pdf/{applicantId}/upload", "/applicant-image/{applicantId}/upload",
						"/applicant-image/getphoto/{applicantId}", "/videos/recommended/{applicantId}",
						"/applicant/closeAccount/{id}", "/applyjob/alert/delete/{alertsId}",
						"/api/mentor-connect/getAllMeetings", "/api/mentor-connect/getMeetingById/{id}","/api/mentor-connect/registerMentorConnect/{mentorConnectId}/applicant/{applicantId}",
						"/applicant-image/getphoto/{applicantId}", "/blogs/active", "/blogs/getBlogById/{id}",
						"/applicant-image/getphoto/{applicantId}", "/api/hackathons/getAllHackathons",
						"/api/hackathons/active", "/api/hackathons/upcoming", "/api/hackathons/completed",
						"/api/hackathons/recommended/{applicantId}",
						"/api/hackathons/getApplicantRegisteredHackathons/{applicantId}",
						"/hackathons/{hackathonId}/getRegistrationStatus/{applicantId}",
						"/api/hackathons/{hackathonId}/submit", "/applicant-image/hackathon/winners",
						"/hackathons/{applicantId}/getAllRegistrationStatus",
						"/hackathons/{hackathonId}/registerForHackathon/{applicantId}", "/aiPrepChat/saveChat",
						"/aiPrepChat/getAllChatTitles/**", "/aiPrepChat/*/getChatDetailsById/**",
						"/aiPrepChat/*/updateChatDetails/**", "/aiPrepChat/*/deleteChat/**",
						"/aiPrepChat/getAllChats/**", "/aiPrepModel/postQuery",  "/videos/search",
						"/applicant-scores/applicant/{applicantId}/getTotalScore",
						"/applicant-scores/applicant/{applicantId}/getApplicantScoreDetails",
						"/applicant-card/**","/applicant-summary/**","/applicant-personal/**","/applicant-education/**","/applicant-projects/**","/applicant-key-skills/**","/applicant/{id}/tour-seen",
						"/notifications/getNotifications/{applicantId}", "/notifications/{notificationId}/deleteNotification/{applicantId}",
						"/notifications/deleteAllNotifications/{applicantId}", "/notifications/{notificationId}/move-to-seen/{applicantId}",
						"/notifications/move-to-seen-everywhere/{applicantId}","/api/feedback-forms/applicant/{applicantId}/**")
				.hasAnyRole("JOBAPPLICANT")
				.antMatchers("/api/hackathons/getHackathonDetails/{hackathonId}/{candidateOrRecruiterId}")
				.hasAnyRole("JOBRECRUITER", "JOBAPPLICANT")
				.antMatchers("/api/analytics/**","/resume/retryResumeRegistration", "/resume/pdf/{id}", "/applicant/getApplicantById/{id}",
						"/send-message", "/health", "/applicant/signOut", "/forgotpassword/recuriterverify-otp",
						"/forgotpassword/recuritersend-otp",
						"/forgotpassword/recuriterreset-password/set-new-password/{email}",
						"/applyjob/applicant/mark-alert-as-seen/{alertsId}", "/recuriters/saverecruiters",
						"/recuriters/recruiterLogin", "/recuriters/registration-send-otp", "/api/gemini/chat",
						"/applicant/saveApplicant", "/applicant/applicantLogin", "/applicant/applicantsendotp",

						"/applicant/applicantverify-otp", "/applicant/applicantreset-password/{email}",
						"/applicant/applicantsignOut", "/applicant/forgotpasswordsendotp", "/swagger-ui/**",
						"/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/getAllJobs",
						"/api/zoho/submit-lead", "/zoho/create-lead", "/zoho/update/{recordId}",
						"/zoho/searchlead/{email}", "/mentorfeedback/form",
					    "/mentorfeedback/form/*",
					    "/mentorfeedback/form/*/submit",
					    "/mentorfeedback/forms",
					    "/mentorfeedback/forms/*",
					    "/mentorfeedback/forms/*/duplicate",
					    "/mentorfeedback/form/*/responses")
				.permitAll()
				// Additional antMatchers for Swagger
				.antMatchers(HttpMethod.GET, "/v2/api-docs", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**")
				.permitAll().anyRequest().authenticated().and().exceptionHandling().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
