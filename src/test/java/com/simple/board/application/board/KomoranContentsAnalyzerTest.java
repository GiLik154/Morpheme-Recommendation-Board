package com.simple.board.application.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KomoranContentsAnalyzerTest {
    private final KomoranContentsAnalyzer komoranContentsAnalyzer;

    @Autowired
    public KomoranContentsAnalyzerTest(KomoranContentsAnalyzer komoranContentsAnalyzer) {
        this.komoranContentsAnalyzer = komoranContentsAnalyzer;
    }

    @Test
    void 한글로_된_형태소는_분석된다() {
        String testString = "아버지가방에들어가시다";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertEquals(2, stringList.size());
        assertEquals("아버지", stringList.get(0));
        assertEquals("가방", stringList.get(1));
    }

    @Test
    void 영어로_된_형태소는_분석된다() {
        String testString = "아버지Bag에들어가시다";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertEquals(2, stringList.size());
        assertEquals("아버지", stringList.get(0));
        assertEquals("Bag", stringList.get(1));
    }

    @Test
    void 유저사전에_저장된_단어도_분석이_된다() {
        String testString = "사랑해요유스방";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertEquals(1, stringList.size());
        assertEquals("사랑해요유스방", stringList.get(0));
    }

    @Test
    void 없는_단어는_분석되지_않는다() {
        String testString = "글룽강";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertTrue(stringList.isEmpty());
    }


    @Test
    void null은_분석이_되지않는다() {
        String testString = "";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertTrue(stringList.isEmpty());
    }

    @Test
    void 형태소는_2글자이상만_분석된다() {
        String testString = "굴";

        List<String> stringList = komoranContentsAnalyzer.analyze(testString);

        assertTrue(stringList.isEmpty());
    }
}