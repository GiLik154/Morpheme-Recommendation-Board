package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.board.domain.RecommendBoard;
import com.simple.board.domain.board.domain.RecommendBoardRepository;
import com.simple.board.domain.morphemecount.domain.MorphemeCount;
import com.simple.board.domain.morphemecount.domain.MorphemeCountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardDeleterImplTest {
    private final BoardDeleter boardDeleter;
    private final BoardRepository boardRepository;
    private final RecommendBoardRepository recommendBoardRepository;
    private final MorphemeCountRepository morphemeCountRepository;

    @Autowired
    public BoardDeleterImplTest(BoardDeleter boardDeleter, BoardRepository boardRepository, RecommendBoardRepository recommendBoardRepository, MorphemeCountRepository morphemeCountRepository) {
        this.boardDeleter = boardDeleter;
        this.boardRepository = boardRepository;
        this.recommendBoardRepository = recommendBoardRepository;
        this.morphemeCountRepository = morphemeCountRepository;
    }

    @Test
    void 게시물_삭제_정상작동() {
        MorphemeCount morphemeCount = new MorphemeCount("origin");
        morphemeCount.increaseCount(1);
        morphemeCount = morphemeCountRepository.save(morphemeCount);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount);

        Board newBoard = new Board("origin", "originContested", morphemeCounts);
        boardRepository.save(newBoard);

        boardDeleter.delete(newBoard.getId());

        Optional<Board> boardOptional = boardRepository.findByTitle("origin");
        long originWordCount = morphemeCountRepository.findById("origin").get().getCount();

        assertEquals(0, originWordCount);
        assertTrue(boardOptional.isEmpty());
    }


    @Test
    void 게시물_삭제_정상작동_추천_모두_삭제() {
        Board newBoard1 = new Board("soon_origin", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard1);
        Long newBoard1Id = newBoard1.getId();

        Board newBoard2 = new Board("test1", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard2);

        Board newBoard3 = new Board("test2", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard3);

        RecommendBoard recommendBoard1 = new RecommendBoard(newBoard1, newBoard2, 3L);
        recommendBoardRepository.save(recommendBoard1);
        RecommendBoard recommendBoard2 = new RecommendBoard(newBoard1, newBoard3, 3L);
        recommendBoardRepository.save(recommendBoard2);

        boardDeleter.delete(newBoard1Id);

        Optional<Board> boardOptional = boardRepository.findByTitle("origin");

        Board board = boardRepository.findByTitle("test2").get();
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();

        assertTrue(boardOptional.isEmpty());
        assertEquals(0, boardList.size());
    }
}