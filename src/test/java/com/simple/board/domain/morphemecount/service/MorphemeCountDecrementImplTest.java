package com.simple.board.domain.morphemecount.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.morphemecount.domain.MorphemeCount;
import com.simple.board.domain.morphemecount.domain.MorphemeCountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MorphemeCountDecrementImplTest {

    private final MorphemeCountDecrement morphemeCountDecrement;
    private final MorphemeCountRepository morphemeCountRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public MorphemeCountDecrementImplTest(MorphemeCountDecrement morphemeCountDecrement, MorphemeCountRepository morphemeCountRepository, BoardRepository boardRepository) {
        this.morphemeCountDecrement = morphemeCountDecrement;
        this.morphemeCountRepository = morphemeCountRepository;
        this.boardRepository = boardRepository;
    }

    @Test
    void 형태소_사용횟수_감소_정상작동() {
        Board board = new Board("testTitle", "testContents", Collections.emptyList());
        boardRepository.save(board);

        MorphemeCount morphemeCount = new MorphemeCount("사랑해요유스방");
        morphemeCount.increaseCount(5);
        morphemeCount = morphemeCountRepository.save(morphemeCount);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount);

        morphemeCountDecrement.decrease(morphemeCounts);

        MorphemeCount testResult = morphemeCountRepository.findById("사랑해요유스방").get();

        assertEquals(0, testResult.getCount());
        assertEquals(0, testResult.getPercent());
    }

    @Test
    void 형태소_감소_2회() {
        Board board = new Board("testTitle", "testContents", Collections.emptyList());
        boardRepository.save(board);

        MorphemeCount morphemeCount = new MorphemeCount("사랑해요유스방");
        morphemeCount.increaseCount(5);
        morphemeCount.increaseCount(5);
        morphemeCount.increaseCount(5);
        morphemeCount = morphemeCountRepository.save(morphemeCount);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount);

        morphemeCountDecrement.decrease(morphemeCounts);
        morphemeCountDecrement.decrease(morphemeCounts);

        MorphemeCount testResult = morphemeCountRepository.findById("사랑해요유스방").get();

        assertNotNull(testResult.getWord());
        assertEquals(1, testResult.getCount());
        assertEquals(100, testResult.getPercent());
    }

    @Test
    void 형태소_사용한적_게시글_점유율_감소() {
        Board board = new Board("testTitle", "testContents", Collections.emptyList());
        boardRepository.save(board);

        Board board2 = new Board("testTitle", "testContents", Collections.emptyList());
        boardRepository.save(board2);

        MorphemeCount morphemeCount = new MorphemeCount("사랑해요유스방");
        morphemeCount.increaseCount(5);
        morphemeCount.increaseCount(5);
        morphemeCount = morphemeCountRepository.save(morphemeCount);

        List<MorphemeCount> morphemeCounts = new ArrayList<>();
        morphemeCounts.add(morphemeCount);

        morphemeCountDecrement.decrease(morphemeCounts);

        MorphemeCount testResult = morphemeCountRepository.findById("사랑해요유스방").get();

        assertNotNull(testResult.getWord());
        assertEquals(1, testResult.getCount());
        assertEquals(50, testResult.getPercent());
    }
}