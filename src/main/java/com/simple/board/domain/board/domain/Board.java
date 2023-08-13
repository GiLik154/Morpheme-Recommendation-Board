package com.simple.board.domain.board.domain;

import com.simple.board.domain.morphemecount.domain.MorphemeCount;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

/**
 * 게시글
 */
@Entity
@Getter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 제목 */
    @Column(length = 255, nullable = false)
    private String title;
    /** 내용 */
    @Column(length = 3000, nullable = false)
    private String contents;

    /** 형태소 색인 */
    @ManyToMany(fetch = FetchType.LAZY)
    private List<MorphemeCount> morphemeCount;

    protected Board() {}

    public Board(String title, String contents, List<MorphemeCount> morphemeCount) {
        this.title = title;
        this.contents = contents;
        this.morphemeCount = morphemeCount;
    }

    public void update(String title, String contents, List<MorphemeCount> morphemeCount) {
        this.title = title;
        this.contents = contents;
        this.morphemeCount = morphemeCount;
    }
}
