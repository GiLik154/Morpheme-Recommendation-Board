package com.simple.board.domain.board.domain;

import lombok.Getter;

import javax.persistence.*;

/**
 * 추천 게시물
 * Board = 메인이 되는 게시물
 * recommendationBoard = 추천되는 게시물
 */
@Entity
@Getter
public class RecommendBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 메인 게시물 */
    @ManyToOne(fetch = FetchType.LAZY)
    private Board originBoard;

    /** 추천 게시물 */
    @ManyToOne(fetch = FetchType.LAZY)
    private Board recommendationBoard;

    /** 일치하는 단어 갯수 */
    private long priority;

    protected RecommendBoard() {}

    public RecommendBoard(Board originBoard, Board recommendationBoard, long priority) {
        this.originBoard = originBoard;
        this.recommendationBoard = recommendationBoard;
        this.priority = priority;
    }
}
