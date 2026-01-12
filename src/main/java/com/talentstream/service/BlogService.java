package com.talentstream.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.Blog;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.BlogRepository;
import com.talentstream.util.ContentType;

@Service
public class BlogService {

	private final NotificationMessageService notificationMessageService;

	private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

	private static final String NEWS_BASE_URL = "https://newsapi.org/v2/everything";
	private static final String GEMINI_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=%s";

	@Value("${newsapi.key}")
	private String apiKey;

	@Value("${gemini.api.key}")
	private String geminiKey;

	private final RestTemplate restTemplate;
	private final BlogRepository blogRepository;
	private final ObjectMapper mapper;
	private final FirebaseMessagingService firebaseMessagingService;
	private final ApplicantProfileRepository applicantProfileRepository;
	private final ApplicantRepository applicantRepository;
	private final ApplicantContentViewsService applicantContentViewsService;

	public BlogService(BlogRepository blogRepository, FirebaseMessagingService firebaseMessagingService,
			NotificationMessageService notificationMessageService,
			ApplicantProfileRepository applicantProfileRepository, ApplicantRepository applicantRepository,
			ApplicantRepository applicantRepository2, ApplicantContentViewsService applicantContentViewsService) {
		this.blogRepository = blogRepository;
		this.restTemplate = new RestTemplate();
		this.mapper = new ObjectMapper();
		this.firebaseMessagingService = firebaseMessagingService;
		this.notificationMessageService = notificationMessageService;
		this.applicantProfileRepository = applicantProfileRepository;
		this.applicantRepository = applicantRepository2;
		this.applicantContentViewsService = applicantContentViewsService;
	}

	public void fetchAndSaveTechNews() {
		logger.debug("fetchAndSaveTechNews - starting news fetch");
		try {
			String url = buildNewsApiUrl();
			logger.debug("fetchAndSaveTechNews - news api url built");

			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				logger.error("fetchAndSaveTechNews - failed to fetch news. Status: {}", response.getStatusCode());
				return;
			}

			JsonNode articles = mapper.readTree(response.getBody()).path("articles");
			logger.debug("fetchAndSaveTechNews - articles node retrieved, size approx: {}", articles.size());

			if (articles.isEmpty()) {
				logger.info("fetchAndSaveTechNews - no new articles found");
				return;
			}

			for (JsonNode article : articles) {
				String articleUrl = article.path("url").asText();
				String title = article.path("title").asText();

				if (blogRepository.findByUrlOrTitle(articleUrl, title).isPresent()) {
					logger.debug("fetchAndSaveTechNews - skipping duplicate article: {}", title);
					continue;
				}

				Blog blog = mapArticleToBlog(article);
				blogRepository.save(blog);
				logger.info("fetchAndSaveTechNews - saved blog: {}", blog.getTitle());
			}

		} catch (Exception e) {
			logger.error("fetchAndSaveTechNews - unexpected error: {}", e.getMessage(), e);
			throw new CustomException("Error fetching or saving tech news: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String buildNewsApiUrl() {
		LocalDate today = LocalDate.now();
		LocalDate tenDaysAgo = today.minusDays(15);
		String query = URLEncoder.encode(
				"Artificial Intelligence OR AI OR Machine Learning OR ML OR "
						+ "Software OR Programming OR Coding OR Technology" + "Information Technology OR IT OR Cloud",
				StandardCharsets.UTF_8);
		return NEWS_BASE_URL + "?apiKey=" + apiKey + "&q=" + query + "&searchIn=title,description,content"
				+ "&domains=techcrunch.com,wired.com,theverge.com,developer-tech.com,ai-news.com,engadget.com"
				+ "&from=" + tenDaysAgo + "&to=" + today + "&language=en" + "&sortBy=publishedAt" + "&pageSize=100"
				+ "&page=1";
	}

	private Blog mapArticleToBlog(JsonNode article) {
		String title = article.path("title").asText();
		String description = article.path("description").asText();
		String content = article.path("content").asText();

		String elaboratedContent = callGeminiForElaboration(title, description, content);

		Blog blog = new Blog();
		blog.setTitle(title);
		blog.setDescription(description);
		blog.setAuthor(article.path("author").asText(null));
		blog.setContent(elaboratedContent);
		blog.setUrl(article.path("url").asText());
		blog.setImageUrl(article.path("urlToImage").asText());
		blog.setPublishedAt(article.path("publishedAt").asText());
		blog.setCreatedAt(LocalDateTime.now().plus(Duration.ofMinutes(330)));
		return blog;
	}

	private String callGeminiForElaboration(String title, String description, String snippet) {
		logger.debug("callGeminiForElaboration - calling Gemini for title={}", title);
		try {
			String prompt = String
					.format("Summarize this news into 8 to 10 simple paragraphs so that it is easy to understand . "
							+ "Keep only the main content with a good simple conclusion about the news"
							+ "Keep the explanation clear, engaging, and informative."
							+ " Do not include headings, bullet points, "
							+ "symbols, links, or tables. Use plain text only.\n\n"
							+ "Title: %s\nDescription: %s\nContent snippet: %s\n\nBlog:", title, description, snippet);

			String geminiUrl = String.format(GEMINI_URL_TEMPLATE, geminiKey);

			// Build JSON safely using Map
			Map<String, Object> part = Map.of("text", prompt);
			Map<String, Object> contents = Map.of("parts", List.of(part));
			Map<String, Object> requestBody = Map.of("contents", List.of(contents));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(geminiUrl, HttpMethod.POST, request, String.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				JsonNode root = mapper.readTree(response.getBody());
				String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
				logger.debug("callGeminiForElaboration - received elaboration for title={}", title);
				return text;
			} else {
				logger.warn("callGeminiForElaboration - non-2xx response or empty body from Gemini for title={}",
						title);
			}

		} catch (Exception e) {
			logger.error("callGeminiForElaboration - error calling Gemini: {}", e.getMessage(), e);
			throw new CustomException("Error calling Gemini API: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return snippet;
	}

	public List<Blog> getActiveBlogs(int page, int size) {
		logger.debug("Inside Service : getActiveBlogs - entry page={}, size={}", page, size);
		try {
			Pageable pageable = PageRequest.of(page, size);
			List<Blog> activeBlogs = blogRepository.findActiveBlogsOrdered(pageable);
			if (activeBlogs.isEmpty()) {
				logger.warn("getActiveBlogs - no active blogs found page={}, size={}", page, size);
				throw new CustomException("No active blogs found", HttpStatus.NOT_FOUND);
			}
			logger.debug("getActiveBlogs - returning {} active blogs", activeBlogs.size());
			return activeBlogs;
		} catch (Exception e) {
			logger.error("getActiveBlogs - error retrieving active blogs: {}", e.getMessage(), e);
			throw new CustomException("Failed to retrieve active blogs", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<Blog> getInActiveBlogs() {
		logger.debug("Inside getInActiveBlogs Service method");
		try {
			List<Blog> inactiveBlogs = blogRepository.findInactiveBlogsOrdered();
			if (inactiveBlogs.isEmpty()) {
				logger.warn("getInActiveBlogs - no inactive blogs found");
				throw new CustomException("No inactive blogs found", HttpStatus.NOT_FOUND);
			}
			logger.debug("getInActiveBlogs - returning {} inactive blogs", inactiveBlogs.size());
			return inactiveBlogs;
		} catch (Exception e) {
			logger.error("getInActiveBlogs - error retrieving inactive blogs: {}", e.getMessage(), e);
			throw new CustomException("Failed to retrieve inactive blogs", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String deleteBlog(Long id) {
		logger.debug("Inside delete Blog Service with blog id={}", id);
		Blog blog = blogRepository.findById(id)
				.orElseThrow(() -> new CustomException("Blog not found with id: " + id, HttpStatus.NOT_FOUND));
		try {
			blogRepository.delete(blog);
			logger.info("deleteBlog - deleted blog id={}", id);
			return "Blog deleted successfully with id: " + id;
		} catch (Exception e) {
			logger.error("deleteBlog - error deleting blog id={}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while deleting the blog with id: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String updateBlog(Long id, String author, boolean active) {

		logger.debug("Inside update Blog Service with blog id={}", id);
		Blog blog = blogRepository.findById(id)
				.orElseThrow(() -> new CustomException("Blog not found with id: " + id, HttpStatus.NOT_FOUND));

		try {
			logger.debug("updateBlog - updating blog id={}, author={}, active={}", id, author, active);
			blog.setAuthor(author);
			blog.setIsActive(active);
			blog.setCreatedAt(LocalDateTime.now().plusMinutes(330));

			blogRepository.save(blog);
			logger.info("updateBlog Service- blog updated id={}", id);
		} catch (Exception e) {
			logger.error("updateBlog - error updating blog id={}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while updating the blog: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		firebaseMessagingService.sendNotificationToAll("New Blog Update Today!",
				"Check out the latest blog updates added today.");

		List<Long> applicantIds = applicantProfileRepository.findApplicantsByApplicantId();
		if (applicantIds.isEmpty()) {
			logger.warn("updateBlog - no applicants found to notify for blog id={}", id);
			throw new CustomException("No applicants found to notify", HttpStatus.NOT_FOUND);
		}

		notificationMessageService.sendNotificationToApplicants(
				"A new tech news article has been added: " + blog.getTitle(), "Tech Vibes", id, applicantIds);

		logger.info("updateBlog - notifications queued for blog id={}", id);

		return "Blog updated successfully with id: " + id;
	}

	public Blog getBlogById(Long id) {
		logger.debug("Inside get Blog By Id Service with blog id={}", id);
		try {
			Blog blog = blogRepository.findById(id)
					.orElseThrow(() -> new CustomException("Blog not found with id: " + id, HttpStatus.NOT_FOUND));
			logger.debug("getBlogById - found blog id={}", id);
			return blog;
		} catch (Exception e) {
			logger.error("getBlogById - error retrieving blog id={}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving the blog with id: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Blog getBlogsByIdForApplicant(Long id, Long applicantId) {
		logger.debug("Inside get Blogs By Id For Applicant Service with blog id={} and applicantId={}", id,
				applicantId);
		Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(
				() -> new CustomException("Applicant not found with id: " + applicantId, HttpStatus.NOT_FOUND));
		Blog blog = blogRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> {
			logger.warn("getBlogsByIdForApplicant - blog id={} not found or inactive", id);
			return new CustomException("Blog not found or inactive with id: " + id, HttpStatus.NOT_FOUND);
		});
		try {
			applicantContentViewsService.recordContentView(applicant, id, ContentType.TECH_VIBES);
			logger.debug("getBlogsByIdForApplicant - found blog id={} for applicantId={}", id, applicantId);
			return blog;
		} catch (Exception e) {
			logger.error("getBlogById - error retrieving blog id={}: {}", id, e.getMessage(), e);
			throw new CustomException("An error occurred while retrieving the blog with id: " + id,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
