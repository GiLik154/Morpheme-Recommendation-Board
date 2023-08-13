package com.simple.board.domain.morphemecount.service;

public interface MorphemeCountPercentRenewer {
    /**
     * 1. 모든 게시물의 갯수를 읽어온다
     * 2. 모든 형태소의 퍼센테이지를 변경한다.
     */
    void renew();
}
