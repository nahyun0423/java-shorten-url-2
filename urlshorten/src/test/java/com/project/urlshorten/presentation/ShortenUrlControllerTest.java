package com.project.urlshorten.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ShortenUrlController.class)
class ShortenUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShortenUrlService shortenUrlService;

    private ShortenUrlDto shortenUrlDto;
    private ShortenUrl shortenUrl;

    @BeforeEach
    void setUp() {
        shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");
        shortenUrlDto = new ShortenUrlDto(shortenUrl);
    }

    @Test
    @DisplayName("originalUrl이 주어졌을 때, 단축 URL을 생성하면, 단축 URL 반환")
    void createShortenUrlTest() throws Exception {
        when(shortenUrlService.createShortKey("https://www.google.co.kr")).thenReturn(shortenUrlDto);

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://www.google.co.kr\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$.shortKey").value("abcd123"));
    }

    @Test
    @DisplayName("shortKey가 주어졌을 때, 단축 URL 조회하면, 원본 URL 정보 반환")
    void getShortenUrlTest() throws Exception {
        when(shortenUrlService.findShortenUrl("abcd123")).thenReturn(shortenUrlDto);

        mockMvc.perform(get("/shorten-url/abcd123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$.shortKey").value("abcd123"));
    }

    @Test
    @DisplayName("전체 단축 URL을 조회하면, 전체 URL 리스트 반환")
    void getAllShortenUrlTest() throws Exception {
        Page<ShortenUrlDto> urlList = new PageImpl<>(List.of(shortenUrlDto));
        when(shortenUrlService.findAllShortenUrl(any(Pageable.class))).thenReturn(urlList);

        mockMvc.perform(get("/shorten-url")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$.content[0].shortKey").value("abcd123"));
    }

    @Test
    @DisplayName("정상적인 page 범위로 전체 단축 URL을 조회하면, 해당 page 출력")
    void validPage_getAllShortenUrlTest() throws Exception {
        mockMvc.perform(get("/shorten-url")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    @DisplayName("page 범위 외의 값으로 전체 단축 URL을 조회하면, 400 예외 발생 및 메시지 출력")
    void invalidPage_getAllShortenUrlTest() throws Exception {
        mockMvc.perform(get("/shorten-url")
                        .param("page", "2")
                        .param("size", "11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("잘못된 입력값입니다. page : 0~100 / size : 1~10 범위 내로 입력하세요."));
    }

    @Test
    @DisplayName("shortKey로 리다이렉트 요청을 하면, 원본 URL로 리다이렉트")
    void redirectShortenUrlTest() throws Exception {
        when(shortenUrlService.increaseRedirectCount("abcd123")).thenReturn(shortenUrl);

        mockMvc.perform(get("/abcd123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://www.google.co.kr"));
    }

    @Test
    @DisplayName("잘못된 URL을 입력했을 때, 400 예외 발생 및 메시지 출력")
    void invalidBadUrlTest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("originalUrl", "invalidUrl");

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 URL 형식이 아닙니다."));
    }

    @Test
    @DisplayName("URL을 입력하지 않았을 때, 400 예외 발생 및 메시지 출력")
    void invalidEmptyUrlTest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("originalUrl", "");

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("URL은 필수 입력값입니다."));
    }
}