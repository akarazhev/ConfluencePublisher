package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageAttachmentRepository extends JpaRepository<PageAttachment, Long> {
    
    List<PageAttachment> findByPageIdOrderByPosition(Long pageId);
    
    void deleteByPageId(Long pageId);
}

