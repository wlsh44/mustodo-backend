package mustodo.backend.todo.ui.dto.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HexColorValidatorTest {

    HexColorValidator validator;

    TestClass testClass;

    @Mock
    HexColor hexColor;

    @Mock
    ConstraintValidatorContext context;

    @BeforeEach
    void init() {
        validator = new HexColorValidator();
        validator.initialize(hexColor);
    }

    @ParameterizedTest(name = "{index} => color: {0}, result: {1}")
    @MethodSource("provideColor")
    void test(String color, boolean expect) {
        //given
        testClass = new TestClass(color);

        //when
        boolean res = validator.isValid(color, context);

        //then
        assertThat(res).isEqualTo(expect);
    }

    private Stream<Arguments> provideColor() {
        return Stream.of(
                Arguments.of("#1AFFa1", true),
                Arguments.of("#F00", true),
                Arguments.of("123456", false),
                Arguments.of("#123abce", false),
                Arguments.of("#afafah", false),
                Arguments.of("#000000", true),
                Arguments.of("#FFFFFF", true)
        );
    }
}

class TestClass {

    @HexColor
    String color;

    public TestClass(String color) {
        this.color = color;
    }
}