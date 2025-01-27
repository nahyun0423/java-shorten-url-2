package com.project.urlshorten;

import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UrlshortenApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ShortenUrlRepository shortenUrlRepository;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        shortenUrlRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 흐름 테스트: URL 생성 → 조회 → 리다이렉트 → 리다이렉트 카운트 증가")
    void fullFlowTest() {
        // 단축 URL 생성
        String originalUrl = "https://www.google.com";
        String shortKey = RestAssured.given()
                .contentType("application/json")
                .body("{\"originalUrl\": \"" + originalUrl + "\"}")
                .when()
                .post("/shorten-url")
                .then()
                .statusCode(200)
                .body("originalUrl", equalTo(originalUrl))
                .body("shortKey", notNullValue())
                .extract()
                .jsonPath()
                .getString("shortKey");

        // 조회
        RestAssured.given()
                .when()
                .get("/get/" + shortKey)
                .then()
                .statusCode(200)
                .body("originalUrl", equalTo(originalUrl))
                .body("shortKey", equalTo(shortKey));

        // 리다이렉트
        RestAssured.given()
                .redirects().follow(false)
                .when()
                .get("/" + shortKey)
                .then()
                .statusCode(302)
                .header("Location", originalUrl);

        // 리다이렉트 카운트 증가
        RestAssured.given()
                .when()
                .get("/get/" + shortKey)
                .then()
                .statusCode(200)
                .body("redirectCount", equalTo(1));
    }

    @Test
    @DisplayName("페이징 테스트 : 주어진 page와 size만큼만 정보 출력")
    void getAllShortenUrlsTest() {
        shortenUrlRepository.save(new ShortenUrl("https://www.google.com", "abcd123"));
        shortenUrlRepository.save(new ShortenUrl("https://www.naver.com", "abcd456"));
        shortenUrlRepository.save(new ShortenUrl("https://www.youtube.com", "abcd789"));
        shortenUrlRepository.save(new ShortenUrl("https://github.com", "abcd000"));

        RestAssured.given()
                .when()
                .get("/get/all?page=0&size=2")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].originalUrl", equalTo("https://www.google.com"))
                .body("content[1].originalUrl", equalTo("https://www.naver.com"));
    }
}
