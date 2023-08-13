package com.simple.board.domain.board.service;

import com.simple.board.domain.board.domain.Board;
import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.board.domain.RecommendBoard;
import com.simple.board.domain.board.domain.RecommendBoardRepository;
import com.simple.board.domain.board.domain.aplication.ContentsAnalyzer;
import com.simple.board.domain.board.service.dto.BoardCreatorCommand;
import com.simple.board.domain.board.service.dto.BoardUpdaterCommand;
import com.simple.board.domain.morphemecount.domain.MorphemeCount;
import com.simple.board.domain.morphemecount.domain.MorphemeCountRepository;
import com.simple.board.domain.morphemecount.service.MorphemeCountDecrement;
import com.simple.board.domain.morphemecount.service.MorphemeCountIncrement;
import com.simple.board.domain.morphemecount.service.MorphemeCountPercentRenewer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService implements BoardCreator, BoardDeleter, BoardRecommender, BoardUpdater {
    private final BoardRepository boardRepository;
    private final RecommendBoardRepository recommendBoardRepository;

    private final ContentsAnalyzer contentsAnalyzer;
    private final MorphemeCountIncrement morphemeCountIncrement;
    private final MorphemeCountDecrement morphemeCountDecrement;
    private final MorphemeCountPercentRenewer morphemeCountPercentRenewer;
    private final MorphemeCountRepository morphemeCountRepository;

    private static final int RECOMMEND_TERMS_PERCENT = 60;

    @Override
    public void create(BoardCreatorCommand command) {
        List<String> morphemes = contentsAnalyzer.analyze(command.getContents());
        List<MorphemeCount> morphemeCounts = morphemeCountIncrement.increase(morphemes);

        Board board = new Board(command.getTitle(), command.getContents(), morphemeCounts);
        boardRepository.save(board);

        recommend(board, morphemes);

        morphemeCountPercentRenewer.renew();
    }


    @Override
    public void update(Long boardId, BoardUpdaterCommand command) {
        List<String> morphemes = contentsAnalyzer.analyze(command.getContents());
        List<MorphemeCount> morphemeCountList = morphemeCountIncrement.increase(morphemes);

        boardRepository.findById(boardId).ifPresent(board -> {
                    morphemeCountDecrement.decrease(board.getMorphemeCount());
                    board.update(command.getTitle(), command.getContents(), morphemeCountList);
                    recommend(board, morphemes);
                }
        );
    }

    @Override
    public void recommend(Board board, List<String> morphemeList) {
        List<String> recommendWord = morphemeCountRepository.findWordByWordInAndPercentLessThan(morphemeList, RECOMMEND_TERMS_PERCENT);

        boardRepository.findRecommendBoardWithCount(recommendWord).forEach(morphemeMatchCounter -> {
            Board relatedBoard = morphemeMatchCounter.getBoard();
            long count = morphemeMatchCounter.getCount();

            registerBoardRecommendations(board, relatedBoard, count);
        });
    }

    /**
     * 형태소가 일치하는 기준에 따라 게시판 추천을 등록한다
     * 주어진 게시판이 추천 게시판과 동일하지 않은 경우,
     * 두 게시판 사이의 추천 관계를 저장한다.
     *
     * @param board 추천을 위한 기준이 되는 게시판.
     * @param relatedBoard 기준 게시판에 대한 추천 게시판.
     * @param count 두 게시판 사이에서 일치하는 형태소의 수.
     */
    private void registerBoardRecommendations(Board board, Board relatedBoard, Long count) {
        Long boardId = board.getId();

        if (!boardId.equals(relatedBoard.getId())) {
            RecommendBoard boardToRelated = new RecommendBoard(board, relatedBoard, count);
            RecommendBoard relatedToBoard = new RecommendBoard(relatedBoard, board, count);

            List<RecommendBoard> recommendBoards = List.of(boardToRelated, relatedToBoard);

            recommendBoardRepository.saveAll(recommendBoards);
        }
    }

    @Override
    public void delete(Long boardId) {
        boardRepository.findById(boardId).ifPresent(board -> {
            morphemeCountDecrement.decrease(board.getMorphemeCount());

            recommendBoardRepository.deleteAllByBoardId(boardId);
            boardRepository.delete(board);

            morphemeCountPercentRenewer.renew();
        });
    }
}
