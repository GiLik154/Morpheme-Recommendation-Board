package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRecommenderTest {
    private final BoardRecommender boardRecommender;
    private final BoardRepository boardRepository;
    private final RecommendBoardRepository recommendBoardRepository;
    private final MorphemeCountRepository morphemeCountRepository;

    @Autowired
    public BoardRecommenderTest(BoardRecommender boardRecommender, BoardRepository boardRepository, RecommendBoardRepository recommendBoardRepository, MorphemeCountRepository morphemeCountRepository) {
        this.boardRecommender = boardRecommender;
        this.boardRepository = boardRepository;
        this.recommendBoardRepository = recommendBoardRepository;
        this.morphemeCountRepository = morphemeCountRepository;
    }

    @Test
    void 게시물_추천_정상작동() {
        MorphemeCount morphemeCount1 = new MorphemeCount("사랑해요유스방");
        morphemeCount1.increaseCount(5);
        morphemeCountRepository.save(morphemeCount1);

        MorphemeCount morphemeCount2 = new MorphemeCount("사랑해요");
        morphemeCount2.increaseCount(5);
        morphemeCountRepository.save(morphemeCount2);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount1);
        morphemeCounts.add(morphemeCount2);

        List<String> strings = new ArrayList<>();
        strings.add(morphemeCount1.getWord());
        strings.add(morphemeCount2.getWord());

        Board recommendBoard = new Board("testTitle1", "testContents1", morphemeCounts);
        boardRepository.save(recommendBoard);

        Board board = new Board("testTitle2", "testContents2", Collections.emptyList());
        boardRepository.save(board);

        boardRecommender.recommend(board, strings);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boards = boardPage.getContent();

        assertFalse(boards.isEmpty());
        assertEquals("testTitle1", boards.get(0).getTitle());
        assertEquals("testContents1", boards.get(0).getContents());
    }
}