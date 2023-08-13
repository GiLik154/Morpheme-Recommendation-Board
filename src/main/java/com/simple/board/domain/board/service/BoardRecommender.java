package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;

import java.util.List;

public interface BoardRecommender {
    /**
     * 1. 형태소가 색인되어있는지 확인한다.
     * 2. 형태소의 사용 횟수를 1씩 증가하고, 퍼센테이지를 업데이트한다.
     * 3. Board에 색인되어 있지 않은 것을 색인한다.
     *
     * @param board 새로 생성된 Borad
     * @param morphemeList Board에서 사용된 형태소
     */
    void recommend(Board board, List<String> morphemeList);
}
