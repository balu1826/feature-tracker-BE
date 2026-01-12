package com.talentstream.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.talentstream.entity.VideoMetadata;
import java.util.List;
import com.talentstream.entity.VideoMetadata;
 
@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadata, Long> {
 
	@Query(
				    value = "WITH keyword_watch_count AS ( " +
				            "    SELECT kw.keyword, COUNT(*) AS watch_count " +
				            "    FROM applicant_video_watch_history wh " +
				            "    JOIN video_metadata v2 ON wh.video_id = v2.video_id " +
				            "    JOIN ( " +
				            "        SELECT 'interview' AS keyword " +
				            "        UNION ALL SELECT 'softskill' " +
				            "        UNION ALL SELECT 'communication' " +
				            "    ) kw ON (',' || LOWER(v2.skill_tag) || ',' LIKE CONCAT('%,', kw.keyword, ',%')) " +
				            "    WHERE wh.applicant_id = :applicantId " +
				            "    GROUP BY kw.keyword " +
				            "), " +
				            "skill_watch_count AS ( " +
				            "    SELECT LOWER(s.skill_name) AS keyword, COUNT(*) AS watch_count " +
				            "    FROM applicant_video_watch_history wh " +
				            "    JOIN video_metadata v2 ON wh.video_id = v2.video_id " +
				            "    JOIN applicant_profile ap ON ap.applicantid = :applicantId " +
				            "    JOIN applicant_profile_skills_required apsr ON apsr.profileid = ap.profileid " +
				            "    JOIN applicant_skills s ON apsr.applicantskill_id = s.id " +
				            "    WHERE (',' || LOWER(v2.skill_tag) || ',' LIKE CONCAT('%,', LOWER(TRIM(s.skill_name)), ',%')) " +
				            "    GROUP BY LOWER(s.skill_name) " +
				            "), " +
				            "combined_watch_count AS ( " +
				            "    SELECT keyword, SUM(watch_count) AS watch_count " +
				            "    FROM ( " +
				            "        SELECT * FROM keyword_watch_count " +
				            "        UNION ALL " +
				            "        SELECT * FROM skill_watch_count " +
				            "    ) all_counts " +
				            "    GROUP BY keyword " +
				            "), " +
				            "applicant_skills_list AS ( " +
				            "    SELECT LOWER(TRIM(s.skill_name)) AS skill_name " +
				            "    FROM applicant_profile ap " +
				            "    JOIN applicant_profile_skills_required apsr ON apsr.profileid = ap.profileid " +
				            "    JOIN applicant_skills s ON apsr.applicantskill_id = s.id " +
				            "    WHERE ap.applicantid = :applicantId " +
				            "), " +
				            "video_scores AS ( " +
				            "    SELECT " +
				            "        v.video_id, v.s3url, v.level, v.title, v.thumbnail_url, v.skill_tag, " +
				            "        v.created_at, " +
				            "        CASE WHEN w.video_id IS NULL THEN 0 ELSE 1 END AS watched_flag, " +
				            "        MAX(CASE WHEN (',' || LOWER(v.skill_tag) || ',' LIKE CONCAT('%,', LOWER(TRIM(s.skill_name)), ',%')) " +
				            "                 THEN COALESCE(cwc.watch_count, 0) ELSE 0 END) AS skill_count, " +
				            "        MAX(CASE WHEN (',' || LOWER(v.skill_tag) || ',' LIKE '%,interview,%' " +
				            "                     OR ',' || LOWER(v.skill_tag) || ',' LIKE '%,softskill,%' " +
				            "                     OR ',' || LOWER(v.skill_tag) || ',' LIKE '%,communication,%') " +
				            "                 THEN COALESCE(cwc.watch_count, 0) ELSE 0 END) AS general_count, " +
				            "        MAX(CASE WHEN (',' || LOWER(v.skill_tag) || ',' LIKE CONCAT('%,', LOWER(TRIM(s.skill_name)), ',%')) THEN 1 ELSE 0 END) AS is_skill, " +
				            "        MAX(CASE WHEN (',' || LOWER(v.skill_tag) || ',' LIKE '%,interview,%' " +
				            "                     OR ',' || LOWER(v.skill_tag) || ',' LIKE '%,softskill,%' " +
				            "                     OR ',' || LOWER(v.skill_tag) || ',' LIKE '%,communication,%') " +
				            "                 THEN 1 ELSE 0 END) AS is_general " +
				            "    FROM video_metadata v " +
				            "    LEFT JOIN applicant_video_watch_history w ON v.video_id = w.video_id AND w.applicant_id = :applicantId " +
				            "    LEFT JOIN applicant_profile ap ON ap.applicantid = :applicantId " +
				            "    LEFT JOIN applicant_profile_skills_required apsr ON apsr.profileid = ap.profileid " +
				            "    LEFT JOIN applicant_skills s ON apsr.applicantskill_id = s.id " +
				            "    LEFT JOIN combined_watch_count cwc ON cwc.keyword = LOWER(s.skill_name) " +
				            "        OR (cwc.keyword IN ('interview','softskill','communication') " +
				            "            AND (',' || LOWER(v.skill_tag) || ',' LIKE CONCAT('%,', cwc.keyword, ',%'))) " +
				            "    GROUP BY v.video_id, v.s3url, v.level, v.title, v.thumbnail_url, v.skill_tag, v.created_at, CASE WHEN w.video_id IS NULL THEN 0 ELSE 1 END " +
				            ") " +
				            "SELECT video_id, s3url, level, title, thumbnail_url, skill_tag, watched_flag, is_skill, is_general, " +
				            "       GREATEST(skill_count, general_count) AS watch_count " +
				            "FROM video_scores v " +
				            "WHERE NOT EXISTS ( " +
				            "    SELECT 1 FROM ( " +
				            "        SELECT 'java' AS skill " +
				            "        UNION ALL SELECT 'c' " +
				            "        UNION ALL SELECT 'c++' " +
				            "        UNION ALL SELECT 'c sharp' " +
				            "        UNION ALL SELECT 'python' " +
				            "        UNION ALL SELECT 'html' " +
				            "        UNION ALL SELECT 'css' " +
				            "        UNION ALL SELECT 'javascript' " +
				            "        UNION ALL SELECT 'typescript' " +
				            "        UNION ALL SELECT 'angular' " +
				            "        UNION ALL SELECT 'react' " +
				            "        UNION ALL SELECT 'vue' " +
				            "        UNION ALL SELECT 'jsp' " +
				            "        UNION ALL SELECT 'servlets' " +
				            "        UNION ALL SELECT 'spring' " +
				            "        UNION ALL SELECT 'spring boot' " +
				            "        UNION ALL SELECT 'hibernate' " +
				            "        UNION ALL SELECT '.net' " +
				            "        UNION ALL SELECT 'django' " +
				            "        UNION ALL SELECT 'flask' " +
				            "        UNION ALL SELECT 'sql' " +
				            "        UNION ALL SELECT 'mysql' " +
				            "        UNION ALL SELECT 'sql-server' " +
				            "        UNION ALL SELECT 'mongo db' " +
				            "        UNION ALL SELECT 'selenium' " +
				            "        UNION ALL SELECT 'regression testing' " +
				            "        UNION ALL SELECT 'manual testing' " +
				            "    ) AS tech " +
				            "    WHERE (',' || LOWER(v.skill_tag) || ',' LIKE '%,' || tech.skill || ',%') " +
				            "    AND tech.skill NOT IN (SELECT skill_name FROM applicant_skills_list) " +
				            ") " +
				            "ORDER BY " +
				            "    watched_flag ASC, " +  
				            "    is_skill DESC, " +   
				            "    is_general DESC, " +
				            "    created_at DESC, " +  
				            "    GREATEST(skill_count, general_count) DESC, " +
				            "    CASE WHEN watched_flag = 1 THEN RANDOM() END, " +
				            "    video_id ASC " +
				            "LIMIT 15",
				    countQuery = "SELECT COUNT(*) FROM video_metadata",
				    nativeQuery = true
				)
 
		List<Object[]> fetchRecommendedVideos(@Param("applicantId") Long applicantId);
		
    @Query("SELECT v FROM VideoMetadata v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.skillTag) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<VideoMetadata> findByTitleContainingIgnoreCaseOrSkillTagContainingIgnoreCase(@Param("query") String query1, @Param("query") String query2);
    
    @Query("SELECT DISTINCT v.title FROM VideoMetadata v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> findDistinctTitlesByKeyword(@Param("keyword") String keyword);
}