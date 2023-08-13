package com.simple.board.domain.board.service;


public interface BoardDeleter {
    /**
     * 1. 게시물의 Id가 존재하는지 확인한다.
     * 2. 추천 목록에서 일치하는 게시물을 모두 삭제한다
     * 3. 게시물을 삭제한다
     *
     * @param boardId 게시물의 Id
     */
    void delete(Long boardId);
}
