package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ShortenUrlController.class)
class ShortenUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void createShortenUrlTest() throws Exception {
        when(shortenUrlService.createShortKey("https://www.google.co.kr")).thenReturn("abcd123");
        when(shortenUrlService.saveShortenUrl(any(ShortenUrl.class))).thenReturn(shortenUrl);

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://www.google.co.kr\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$.shortKey").value("abcd123"));
    }

    @Test
    void getShortenUrlTest() throws Exception {
        when(shortenUrlService.findShortenUrl("abcd123")).thenReturn(shortenUrl);

        mockMvc.perform(get("/get/abcd123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$.shortKey").value("abcd123"));
    }

    @Test
    void getAllShortenUrlTest() throws Exception {
        List<ShortenUrlDto> urlList = Collections.singletonList(shortenUrlDto);
        when(shortenUrlService.findAllShortenUrl()).thenReturn(urlList);

        mockMvc.perform(get("/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalUrl").value("https://www.google.co.kr"))
                .andExpect(jsonPath("$[0].shortKey").value("abcd123"));
    }

    @Test
    void redirectShortenUrlTest() throws Exception {
        when(shortenUrlService.findShortenUrl("abcd123")).thenReturn(shortenUrl);

        mockMvc.perform(get("/abcd123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://www.google.co.kr"));
    }
}