package com.simple.board.domain.morphemecount.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class MorphemeCount {
    @Id
    private String word;

    private long count = 0L;

    private int percent = 0;

    protected MorphemeCount() {}

    public MorphemeCount(String word) {
        this.word = word;
    }

    public void increaseCount(long totalBoardCount) {
        this.count++;
        calculatePercent(totalBoardCount);
    }

    public void decreaseCount(long totalBoardCount) {
        this.count--;
        calculatePercent(totalBoardCount);
    }

    public void calculatePercent(long totalBoardCount) {
        if ((100.0 * this.count / totalBoardCount) >= 100) {
            this.percent = 100;
        } else if ((100.0 * this.count / totalBoardCount) <= 0) {
            this.percent = 0;
        } else {
            this.percent = (int) (100.0 * this.count / totalBoardCount);
        }
    }
}
