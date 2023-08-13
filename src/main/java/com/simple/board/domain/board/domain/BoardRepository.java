package com.simple.board.domain.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAll(Pageable pageable);

    Optional<Board> findByTitle(String title);

    @Query("SELECT b as board," +
            " COUNT(m) as count" +
            " FROM Board b" +
            " JOIN b.morphemeCount m" +
            " WHERE m.word IN :morphemeNames" +
            " GROUP BY b HAVING COUNT(m) >= 2")
    List<MorphemeMatchCounter> findRecommendBoardWithCount(@Param("morphemeNames") List<String> morphemeNames);
}
