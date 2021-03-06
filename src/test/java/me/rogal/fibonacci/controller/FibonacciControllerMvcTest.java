package me.rogal.fibonacci.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FibonacciControllerMvcTest {

    public static final String BASIC_CREDENTIAL_HEADER = "Basic YWRtaW46czNjcjN0";
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCalculate() throws Exception {
        calculateRequest(0L, List.of());
        calculateRequest(1L, List.of(1L));
        calculateRequest(2L, List.of(1L, 1L));
        calculateRequest(3L, List.of(1L, 1L, 2L));
        calculateRequest(4L, List.of(1L, 1L, 2L, 3L));
        calculateRequest(5L, List.of(1L, 1L, 2L, 3L, 5L));
        calculateRequest(6L, List.of(1L, 1L, 2L, 3L, 5L, 8L));
    }

    private void calculateRequest(Long inputNumber, List<Long> expected) throws Exception {
        mockMvc.perform(get("/api/calculate/{number}", inputNumber)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("authorization", BASIC_CREDENTIAL_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().json(expected.toString()));
    }

    @Test
    public void testMinimumNumberWhenCalculate() {
        validateErrorCalculateRequest(-1L, "must be greater than or equal to 0");
    }

    @Test
    public void testMaximumNumberWhenCalculate() {
        validateErrorCalculateRequest(41L, "must be less than or equal to 40");
    }

    private void validateErrorCalculateRequest(Long negativeNumber, String expectedValidationMessage) {
        try {
            mockMvc.perform(get("/api/calculate/{number}", negativeNumber)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("authorization", BASIC_CREDENTIAL_HEADER))
                    .andExpect(status().is4xxClientError());
            fail("should validate invalid request");
        } catch (Exception e) {
            assertThat(e.getLocalizedMessage()).contains(expectedValidationMessage);
        }
    }
}