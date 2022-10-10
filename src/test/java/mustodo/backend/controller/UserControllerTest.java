package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import mustodo.backend.dto.ErrorDto;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.exception.UserException;
import mustodo.backend.service.user.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.UnsupportedEncodingException;

import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_NAME;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.BasicResponseMsg.INVALID_ARGUMENT_ERROR;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

        SignUpRequestDto dto;

        @BeforeEach
        void init() {
            dto = SignUpRequestDto.builder()
                    .email("test@test.test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(true)
                    .build();
        }

        @Test
        @DisplayName("회원가입 성공")
        void signUpTest() throws Exception {
            //given
            MessageDto expect = MessageDto.builder()
                    .message(SIGN_UP_SUCCESS)
                    .build();
            given(authService.signUp(dto))
                    .willReturn(expect);

            //when
            MvcResult mvcResult = mockMvc.perform(post("/user")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsBytes(dto)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();

            //then
            MessageDto messageDto = getMessageDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 형식 오류")
        void signUpFailTest_bindingException() throws Exception {
            //given
            dto.setEmail("test");
            ErrorDto expect = ErrorDto.builder()
                    .message(INVALID_ARGUMENT_ERROR)
                    .build();

            //when
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //then
            ErrorDto messageDto = getErrorDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
        void signUpFailTest_alreadyExistsEmail() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, ALREADY_EXISTS_EMAIL));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(ALREADY_EXISTS_EMAIL.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이름")
        void signUpFailTest_alreadyExistsName() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, ALREADY_EXISTS_NAME));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(ALREADY_EXISTS_NAME.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 약관 동의 안 함")
        void signUpFailTest_uncheckedTermsAndCondition() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, UNCHECK_TERMS_AND_CONDITION));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(UNCHECK_TERMS_AND_CONDITION.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 확인 실패")
        void signUpFailTest_passwordNotCorrect() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, PASSWORD_CONFIRM_FAILED));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(PASSWORD_CONFIRM_FAILED.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }
    }

    private DocumentContext getParsedContent(MvcResult mvcResult) throws UnsupportedEncodingException {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        return JsonPath.parse(contentAsString);
    }

    private MessageDto getMessageDto(MvcResult mvcResult) throws java.io.IOException {
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        return objectMapper.readValue(contentAsByteArray, MessageDto.class);
    }

    private ErrorDto getErrorDto(MvcResult mvcResult) throws java.io.IOException {
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        return objectMapper.readValue(contentAsByteArray, ErrorDto.class);
    }
}