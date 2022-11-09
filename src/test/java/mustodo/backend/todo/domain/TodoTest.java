package mustodo.backend.todo.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

    @Test
    void 할_일_체크1() {
        //given
        Todo todo = new Todo();

        //when
        todo.checkAchieve();

        //then
        assertThat(todo.isAchieve()).isTrue();
    }
}