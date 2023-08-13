package com.simple.board.application.board;

import com.simple.board.domain.board.domain.aplication.ContentsAnalyzer;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KomoranContentsAnalyzer implements ContentsAnalyzer {
    Komoran komoran = null;

    @PostConstruct
    public void init() {
        komoran = new Komoran(DEFAULT_MODEL.STABLE);
        komoran.setUserDic("user_date/dic.user");
    }

    @Override
    public List<String> analyze(String contents) {
        KomoranResult analyzeResultList = komoran.analyze(contents);

        List<String> anlyzerWordList = analyzeResultList.getMorphesByTags(Arrays.asList("NNG", "NNP", "SL"));

        return anlyzerWordList.stream()
                .distinct()
                .filter(s -> s.length() >= 2)
                .collect(Collectors.toList());
    }
}