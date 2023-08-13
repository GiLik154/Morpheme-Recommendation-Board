package com.simple.board.domain.morphemecount.service;

import com.simple.board.domain.morphemecount.domain.MorphemeCount;

import java.util.List;

public interface MorphemeCountDecrement {
    /**
     * 1. 받아온 형태소의 사용횟수를 1씩 감소한다.
     * 2. 퍼센테이지를 조정한다
     *
     * @param morphemeCounts 형태소
     */
    void decrease(List<MorphemeCount> morphemeCounts);
}
