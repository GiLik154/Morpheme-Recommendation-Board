package com.simple.board.domain.morphemecount.service;

import com.simple.board.domain.morphemecount.domain.MorphemeCount;

public interface MorphemeCountRegister {
    /**
     * 1. 받아온 단어가 색인되어 있는지 확인한다.
     * 2. 색인되어 있지 않으면 색인한다.
     *
     * @param word 단어
     * @return 색인된 형태소
     */
    MorphemeCount register(String word);
}
