package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.talentstream.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {

	@Query("SELECT b FROM Blog b WHERE b.isActive = true ORDER BY b.createdAt DESC")
	List<Blog> findActiveBlogsOrdered(Pageable pageable);

	@Query("SELECT b FROM Blog b WHERE b.isActive = false ORDER BY b.createdAt DESC")
	List<Blog> findInactiveBlogsOrdered();

	@Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
	List<Blog> findAllBlogsOrdered();

	Optional<Blog> findByUrlOrTitle(String url, String title);

	Optional<Blog> findByIdAndIsActiveTrue(Long id);
}
