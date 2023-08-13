package com.simple.board.domain.board.service;

import com.simple.board.domain.board.service.dto.BoardCreatorCommand;

public interface BoardCreator {
    /**
     * 1. 커맨드의 내용을 분석한다.
     * 2. 형태소 단위로 분리하고 색인한다.
     * 3. 추천 알고리즘을 적용한다
     * 4. 커맨드를 통해 Board를 저장한다.
     *
     * @param boardCreatorCommand 생성 정보가 담킨 커맨드
     */
    void create(BoardCreatorCommand boardCreatorCommand);
}
