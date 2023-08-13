package com.simple.board.domain.morphemecount.service;

import com.simple.board.domain.board.domain.BoardRepository;
import com.simple.board.domain.morphemecount.domain.MorphemeCount;
import com.simple.board.domain.morphemecount.domain.MorphemeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MorphemeService implements MorphemeCountRegister, MorphemeCountPercentRenewer, MorphemeCountDecrement, MorphemeCountIncrement {
    private final BoardRepository boardRepository;

    private final MorphemeCountRepository morphemeCountRepository;

    @Override
    public MorphemeCount register(String word) {
        if (!morphemeCountRepository.existsById(word)) {
            MorphemeCount morphemeCount = new MorphemeCount(word);
            morphemeCountRepository.save(morphemeCount);
        }

        return morphemeCountRepository.findById(word).orElseThrow(IllegalStateException::new);
    }

    @Override
    public void renew() {
        long totalBoardCount = boardRepository.count();

        morphemeCountRepository.findAll().forEach(morphemeCount ->
                morphemeCount.calculatePercent(totalBoardCount));
    }

    @Override
    public List<MorphemeCount> increase(List<String> morphemeCounts) {
        return morphemeCounts.stream()
                .map(this::getIncreaseCount)
                .collect(Collectors.toList());
    }

    private MorphemeCount getIncreaseCount(String morpheme) {
        long totalBoardCount = boardRepository.count();

        MorphemeCount morphemeCount = morphemeCountRepository.findById(morpheme)
                .orElseGet(() -> register(morpheme));

        morphemeCount.increaseCount(totalBoardCount);

        return morphemeCount;
    }

    @Override
    public void decrease(List<MorphemeCount> morphemeCounts) {
        long totalBoardCount = boardRepository.count();

        morphemeCounts.forEach(morphemeCount ->
                morphemeCount.decreaseCount(totalBoardCount));
    }
}
