package com.simple.board.domain.board.service.dto;

import lombok.Getter;

@Getter
public class BoardCreatorCommand { // 커맨드 나누기
    private final String title;
    private final String contents;

    public BoardCreatorCommand(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
