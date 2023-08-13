package com.simple.board.domain.morphemecount.service;

import com.simple.board.domain.morphemecount.domain.MorphemeCountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MorphemeCountRegisterImplTest {
    private final MorphemeCountRegister morphemeCountRegister;
    private final MorphemeCountRepository morphemeCountRepository;

    @Autowired
    public MorphemeCountRegisterImplTest(MorphemeCountRegister morphemeCountRegister, MorphemeCountRepository morphemeCountRepository) {
        this.morphemeCountRegister = morphemeCountRegister;
        this.morphemeCountRepository = morphemeCountRepository;
    }

    @Test
    void 형태소_생성_정상작동() {
        String testString = "사랑해요유스방";

        morphemeCountRegister.register(testString);

        assertTrue(morphemeCountRepository.existsById("사랑해요유스방"));
    }
}