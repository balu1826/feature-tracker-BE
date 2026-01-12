package com.talentstream.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.BlogRequestDTO;
import com.talentstream.entity.Blog;
import com.talentstream.service.BlogService;

@RestController
@RequestMapping("/blogs")
public class BlogController {

	private final BlogService blogService;
	private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

	public BlogController(BlogService blogService) {
		this.blogService = blogService;
	}

	@GetMapping("/active")
	public ResponseEntity<?> getActiveBlogs(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size) {
		logger.debug("Received request to get active blogs with page={} and size={}", page, size);
		List<Blog> activeBlogs = blogService.getActiveBlogs(page, size);
		logger.debug("Returning response with {} active blogs", activeBlogs.size());
		return ResponseEntity.ok(activeBlogs);
	}

	@GetMapping("/inactive")
	public ResponseEntity<?> getInActiveBlogs() {
		logger.debug("Received request to get inactive blogs");
		List<Blog> inactiveBlogs = blogService.getInActiveBlogs();
		logger.debug("Returning response with {} inactive blogs", inactiveBlogs.size());
		return ResponseEntity.ok(inactiveBlogs);
	}

	@PutMapping("/updateOrDelete")
	public ResponseEntity<String> updateOrDeleteBlog(@Valid @RequestBody BlogRequestDTO blogRequest,
			BindingResult result) {

		logger.debug("Received request to updateOrDelete blog with id={}, isActive={}", blogRequest.getId(),
				blogRequest.getIsActive());
		if (result.hasErrors()) {
			StringBuilder errors = new StringBuilder();
			result.getFieldErrors().forEach(err -> errors.append(err.getField()).append(" - ")
					.append(err.getDefaultMessage()).append(System.lineSeparator()));
			logger.warn("updateOrDeleteBlog - validation failed: {}", errors.toString());
			return ResponseEntity.badRequest().body(errors.toString());
		}
		if (blogRequest.getIsActive()) {
			logger.debug("Updating blog with id={}", blogRequest.getId());
			blogService.updateBlog(blogRequest.getId(), blogRequest.getAuthor(), blogRequest.getIsActive());
			logger.info("Updated blog with id={}", blogRequest.getId());
			return ResponseEntity.ok("Blog updated successfully with id: " + blogRequest.getId());
		} else {
			logger.debug("Deleting blog with id={}", blogRequest.getId());
			String deletedResult = blogService.deleteBlog(blogRequest.getId());
			logger.info("Deleted blog with id={}", blogRequest.getId());
			return ResponseEntity.ok().body(deletedResult);
		}
	}

	@GetMapping("/getBlogById/{id}")
	public ResponseEntity<?> getBlogById(@PathVariable Long id) {
		logger.debug("Received request to get blog by id={}", id);
		Blog blog = blogService.getBlogById(id);
		logger.debug("Returning blog with id={}", id);
		return ResponseEntity.ok(blog);
	}

	@GetMapping("/getBlogsById/{id}/{applicantId}")
	public ResponseEntity<?> getBlogsByIdForApplicant(@PathVariable Long id, @PathVariable Long applicantId) {
		logger.debug("Received request to get blogs by id={} and applicantId={}", id, applicantId);
		Blog blogs = blogService.getBlogsByIdForApplicant(id, applicantId);
		logger.debug("Returning blogs with id={} and applicantId={}", id, applicantId);
		return ResponseEntity.ok(blogs);
	}
}