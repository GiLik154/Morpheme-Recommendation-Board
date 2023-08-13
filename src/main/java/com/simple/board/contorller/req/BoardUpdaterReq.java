package com.simple.board.contorller.req;

import com.simple.board.domain.board.service.dto.BoardUpdaterCommand;
import lombok.Getter;

@Getter
public class BoardUpdaterReq {
    private final String title;
    private final String contents;

    public BoardUpdaterReq(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public BoardUpdaterCommand encodingServiceCommand() {
        return new BoardUpdaterCommand(this.title, this.contents);
    }
}
