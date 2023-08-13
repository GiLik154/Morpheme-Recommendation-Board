package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.board.domain.RecommendBoardRepository;
import com.simple.board.domain.board.service.dto.BoardUpdaterCommand;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardUpdaterImplTest {
    private final BoardUpdater boardUpdater;
    private final BoardRepository boardRepository;
    private final RecommendBoardRepository recommendBoardRepository;
    private final MorphemeCountRepository morphemeCountRepository;

    @Autowired
    public BoardUpdaterImplTest(BoardUpdater boardUpdater, BoardRepository boardRepository, RecommendBoardRepository recommendBoardRepository, MorphemeCountRepository morphemeCountRepository) {
        this.boardUpdater = boardUpdater;
        this.boardRepository = boardRepository;
        this.recommendBoardRepository = recommendBoardRepository;
        this.morphemeCountRepository = morphemeCountRepository;
    }

    @Test
    void 게시물_업데이트_정상작동() {
        MorphemeCount morphemeCount = new MorphemeCount("origin");
        morphemeCount.increaseCount(1);
        morphemeCount = morphemeCountRepository.save(morphemeCount);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount);

        Board newBoard = new Board("origin", "originContested", morphemeCounts);
        boardRepository.save(newBoard);

        BoardUpdaterCommand boardUpdaterCommand = new BoardUpdaterCommand("update", "아빠가방에들어가신다");
        boardUpdater.update(newBoard.getId(), boardUpdaterCommand);

        Board board = boardRepository.findByTitle("update").get();
        long originWordCount = morphemeCountRepository.findById("origin").get().getCount();

        assertEquals(0, originWordCount);
        assertEquals(2, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();

        assertEquals("update", board.getTitle());
        assertEquals("아빠가방에들어가신다", board.getContents());
    }

    @Test
    void 게시물_업데이트_추천_갱신_정상작동() {
        MorphemeCount morphemeCount1 = new MorphemeCount("origin");
        morphemeCount1.increaseCount(4);
        MorphemeCount morphemeCount2 = new MorphemeCount("test");
        morphemeCount2.increaseCount(4);
        morphemeCountRepository.save(morphemeCount2);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount1);
        morphemeCounts.add(morphemeCount2);

        Board newBoard1 = new Board("soon_origin", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard1);

        Board newBoard2 = new Board("test1", "originContested", morphemeCounts);
        boardRepository.save(newBoard2);

        Board newBoard3 = new Board("test2", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard3);

        Board newBoard4 = new Board("test3", "originContested", Collections.EMPTY_LIST);
        boardRepository.save(newBoard4);

        BoardUpdaterCommand boardUpdaterCommand = new BoardUpdaterCommand("update", "origin test");
        boardUpdater.update(newBoard1.getId(), boardUpdaterCommand);

        Board board = boardRepository.findByTitle("update").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();
        Board recommendBoard = boardList.get(0);

        assertEquals(2, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();

        assertEquals("update", board.getTitle());
        assertEquals("origin test", board.getContents());
        assertEquals(1, boardList.size());

        assertNotEquals(board.getId(), recommendBoard.getId());
        assertEquals("test1", recommendBoard.getTitle());
        assertEquals("originContested", recommendBoard.getContents());
        assertTrue(60 > recommendBoard.getMorphemeCount().get(0).getPercent());
        assertTrue(60 > recommendBoard.getMorphemeCount().get(1).getPercent());
    }
}