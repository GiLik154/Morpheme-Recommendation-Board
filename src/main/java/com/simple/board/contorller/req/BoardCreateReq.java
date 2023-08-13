package com.simple.board.contorller.req;

import com.simple.board.domain.board.service.dto.BoardCreatorCommand;
import lombok.Getter;

@Getter
public class BoardCreateReq {
    private final String title;
    private final String contents;

    public BoardCreateReq(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public BoardCreatorCommand encodingServiceCommand() {
        return new BoardCreatorCommand(this.title, this.contents);
    }
}
