package com.simple.board.domain.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendBoardRepository extends JpaRepository<RecommendBoard, Long> {
    @Query("SELECT rb.recommendationBoard " +
            "FROM RecommendBoard rb " +
            "WHERE rb.originBoard.id = :boardId " +
            "ORDER BY rb.priority DESC")
    Page<Board> findByOriginBoardIdOrderByPriorityDesc(@Param("boardId") Long boardId, Pageable pageable);

    default void deleteAllByBoardId(Long boardId) {
        deleteAllByOriginBoardId(boardId);
        deleteAllByRecommendationBoardId(boardId);
    }

    void deleteAllByOriginBoardId(Long boardId);

    void deleteAllByRecommendationBoardId(Long boardId);
}