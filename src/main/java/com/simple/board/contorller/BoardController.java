package com.simple.board.contorller;

import com.simple.board.contorller.req.BoardCreateReq;
import com.simple.board.contorller.req.BoardUpdaterReq;
import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.board.domain.RecommendBoardRepository;
import com.simple.board.domain.board.service.BoardCreator;
import com.simple.board.domain.board.service.BoardDeleter;
import com.simple.board.domain.board.service.BoardUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardRepository boardRepository;

    private final BoardCreator creator;
    private final BoardUpdater updater;
    private final BoardDeleter deleter;
    private final RecommendBoardRepository recommendBoardRepository;

    @GetMapping("/list")
    public String showBoardListPage(@PageableDefault Pageable pageable, Model model) {
        Page<Board> boards = boardRepository.findAll(pageable);

        model.addAttribute("boards", boards);
        return "thymeleaf/list";
    }

    @GetMapping("/read/{boardId}")
    public String showReadPage(@PathVariable("boardId") Long boardId, @PageableDefault Pageable pageable, Model model) {
        Page<Board> recommendBoards = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(boardId, pageable);

        boardRepository.findById(boardId).ifPresent(board ->
                model.addAttribute("board", board));

        model.addAttribute("recommendBoards", recommendBoards);
        return "thymeleaf/read";
    }

    @GetMapping("/create")
    public String showCreatePage() {
        return "thymeleaf/create";
    }

    @PostMapping("/create")
    public String crate(BoardCreateReq boardCreateReq) {
        creator.create(boardCreateReq.encodingServiceCommand());
        return "redirect:/board/list";
    }

    @DeleteMapping("/delete/{boardId}")
    public String showDeletePage(@PathVariable("boardId") Long boardId) {
        deleter.delete(boardId);
        return "redirect:/board/list";
    }

    @GetMapping("/update/{boardId}")
    public String showUpdatePage(@PathVariable("boardId") Long boardId, Model model) {
        boardRepository.findById(boardId).ifPresent(board ->
                model.addAttribute("board", board));
        return "thymeleaf/update";
    }

    @PutMapping("/update/{boardId}")
    public String update(@PathVariable("boardId") Long boardId, BoardUpdaterReq boardUpdaterReq) {
        updater.update(boardId, boardUpdaterReq.encodingServiceCommand());

        return "redirect:/board/read/" + boardId;
    }
}
