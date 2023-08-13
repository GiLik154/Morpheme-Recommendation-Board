package com.simple.board.domain.morphemecount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MorphemeCountRepository extends JpaRepository<MorphemeCount, String> {

    @Query("SELECT mc.word FROM MorphemeCount mc WHERE mc.word IN :ids AND mc.percent < :percent")
    List<String> findWordByWordInAndPercentLessThan(@Param("ids") List<String> ids, @Param("percent") int percent);
}
