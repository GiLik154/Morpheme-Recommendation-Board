package com.simple.board.domain.board.service.dto;

import lombok.Getter;

@Getter
public class BoardUpdaterCommand {
    private final String title;
    private final String contents;

    public BoardUpdaterCommand(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
