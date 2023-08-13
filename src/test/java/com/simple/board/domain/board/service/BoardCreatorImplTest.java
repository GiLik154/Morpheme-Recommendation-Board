package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.board.domain.RecommendBoardRepository;
import com.simple.board.domain.board.service.dto.BoardCreatorCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardCreatorImplTest {
    private final BoardCreator boardCreator;
    private final BoardRepository boardRepository;
    private final RecommendBoardRepository recommendBoardRepository;

    @Autowired
    public BoardCreatorImplTest(BoardCreator boardCreator, BoardRepository boardRepository, RecommendBoardRepository recommendBoardRepository) {
        this.boardCreator = boardCreator;
        this.boardRepository = boardRepository;
        this.recommendBoardRepository = recommendBoardRepository;
    }

    @Test
    void 게시물_생성_정상작동() {
        BoardCreatorCommand boardCreatorCommand = new BoardCreatorCommand("test", "아빠가방에들어가신다");
        boardCreator.create(boardCreatorCommand);

        Board board = boardRepository.findByTitle("test").get();

        assertNotNull(board.getId());
        assertEquals(2, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();

        assertEquals("test", board.getTitle());
        assertEquals("아빠가방에들어가신다", board.getContents());
    }

    @Test
    void 게시물_생성_추천_알고리즘_작동() {
        Board initBoard1 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard1);

        Board initBoard2 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard2);

        Board initBoard3 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard3);

        BoardCreatorCommand boardCreatorCommand1 = new BoardCreatorCommand("test1", "나는닭가슴살을먹었지만토마토파인애플도먹었다");
        boardCreator.create(boardCreatorCommand1);

        BoardCreatorCommand boardCreatorCommand2 = new BoardCreatorCommand("test2", "닭가슴살토마토");
        boardCreator.create(boardCreatorCommand2);

        Board board = boardRepository.findByTitle("test2").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();
        Board recommendBoard = boardList.get(0);

        assertNotNull(board.getId());
        assertEquals("test2", board.getTitle());
        assertEquals("닭가슴살토마토", board.getContents());
        assertEquals(2, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();

        assertEquals(1, boardList.size());
        assertNotEquals(board.getId(), recommendBoard.getId());
        assertEquals("test1", recommendBoard.getTitle());
        assertEquals("나는닭가슴살을먹었지만토마토파인애플도먹었다", recommendBoard.getContents());
        assertTrue(60 > recommendBoard.getMorphemeCount().get(0).getPercent());
        assertTrue(60 > recommendBoard.getMorphemeCount().get(1).getPercent());
    }

    @Test
    void 게시물_생성_추천_알고리즘_작동_추천2개() {
        Board initBoard1 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard1);

        Board initBoard2 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard2);

        Board initBoard3 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard3);

        Board initBoard4 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard4);

        BoardCreatorCommand boardCreatorCommand1 = new BoardCreatorCommand("test1", "닭가슴살토마토");
        boardCreator.create(boardCreatorCommand1);

        BoardCreatorCommand boardCreatorCommand2 = new BoardCreatorCommand("test2", "나는닭가슴살을먹었지만토마토파인애플도먹었다");
        boardCreator.create(boardCreatorCommand2);

        BoardCreatorCommand boardCreatorCommand3 = new BoardCreatorCommand("test3", "나는닭가슴살을먹었지만토마토파인애플도먹었다");
        boardCreator.create(boardCreatorCommand3);

        Board board = boardRepository.findByTitle("test2").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();
        Board recommendBoard1 = boardList.get(0);
        Board recommendBoard2 = boardList.get(1);

        assertNotNull(board.getId());
        assertEquals("test2", board.getTitle());
        assertEquals("나는닭가슴살을먹었지만토마토파인애플도먹었다", board.getContents());
        assertEquals(3, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(2).getCount()).isNotZero();

        assertEquals(2, boardList.size());

        assertNotEquals(board.getId(), recommendBoard1.getId());
        assertEquals("test3", recommendBoard1.getTitle());
        assertEquals("나는닭가슴살을먹었지만토마토파인애플도먹었다", recommendBoard1.getContents());
        assertTrue(60 > recommendBoard1.getMorphemeCount().get(0).getPercent());
        assertTrue(60 > recommendBoard1.getMorphemeCount().get(1).getPercent());
        assertTrue(60 > recommendBoard1.getMorphemeCount().get(2).getPercent());

        assertNotEquals(board.getId(), recommendBoard2.getId());
        assertEquals("test1", recommendBoard2.getTitle());
        assertEquals("닭가슴살토마토", recommendBoard2.getContents());
        assertTrue(60 > recommendBoard2.getMorphemeCount().get(0).getPercent());
        assertTrue(60 > recommendBoard2.getMorphemeCount().get(1).getPercent());
    }

    @Test
    void 게시물_생성_추천_알고리즘_작동_점유율_60이상_이하_섞임() {
        Board initBoard1 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard1);

        Board initBoard2 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard2);

        Board initBoard3 = new Board("init", "init", Collections.EMPTY_LIST);
        boardRepository.save(initBoard3);

        BoardCreatorCommand boardCreatorCommand1 = new BoardCreatorCommand("test1", "닭가슴살토마토파인애플달걀");
        boardCreator.create(boardCreatorCommand1);

        BoardCreatorCommand boardCreatorCommand2 = new BoardCreatorCommand("test2", "닭가슴살토마토파인애플");
        boardCreator.create(boardCreatorCommand2);

        BoardCreatorCommand boardCreatorCommand3 = new BoardCreatorCommand("test3", "닭가슴살");
        boardCreator.create(boardCreatorCommand3);

        BoardCreatorCommand boardCreatorCommand4 = new BoardCreatorCommand("test4", "닭가슴살");
        boardCreator.create(boardCreatorCommand4);

        BoardCreatorCommand boardCreatorCommand5 = new BoardCreatorCommand("test4", "닭가슴살");
        boardCreator.create(boardCreatorCommand5);

        Board board = boardRepository.findByTitle("test1").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();
        Board recommendBoard1 = boardList.get(0);

        assertNotNull(board.getId());
        assertEquals("test1", board.getTitle());
        assertEquals("닭가슴살토마토파인애플달걀", board.getContents());
        assertEquals(4, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(2).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(3).getCount()).isNotZero();

        assertEquals(1, boardList.size());

        assertNotEquals(board.getId(), recommendBoard1.getId());
        assertEquals("test2", recommendBoard1.getTitle());
        assertEquals("닭가슴살토마토파인애플", recommendBoard1.getContents());
        assertTrue(60 <= recommendBoard1.getMorphemeCount().get(0).getPercent());
        assertTrue(60 > recommendBoard1.getMorphemeCount().get(1).getPercent());
        assertTrue(60 > recommendBoard1.getMorphemeCount().get(2).getPercent());
    }

    @Test
    void 게시물_생성_추천_점유율_60이상() {
        BoardCreatorCommand boardCreatorCommand1 = new BoardCreatorCommand("test1", "나는닭가슴살을먹었지만토마토파인애플도먹었다");
        boardCreator.create(boardCreatorCommand1);

        BoardCreatorCommand boardCreatorCommand2 = new BoardCreatorCommand("test2", "닭가슴살토마토");
        boardCreator.create(boardCreatorCommand2);

        Board board = boardRepository.findByTitle("test2").get();
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priority"));

        Page<Board> boardPage = recommendBoardRepository.findByOriginBoardIdOrderByPriorityDesc(board.getId(), pageable);
        List<Board> boardList = boardPage.getContent();

        assertNotNull(board.getId());
        assertEquals("test2", board.getTitle());
        assertEquals("닭가슴살토마토", board.getContents());
        assertEquals(2, board.getMorphemeCount().size());
        assertThat(board.getMorphemeCount().get(0).getCount()).isNotZero();
        assertThat(board.getMorphemeCount().get(1).getCount()).isNotZero();
        assertTrue(60 < board.getMorphemeCount().get(0).getPercent());
        assertTrue(60 < board.getMorphemeCount().get(1).getPercent());

        assertEquals(0, boardList.size());
    }
}