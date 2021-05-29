package tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import models.AuthorisationResponse;
import models.Books;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("REST API https://demoqa.com tests")
public class BookStoreTests {
    @Test
    @Tag("api")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Get test (GET Books)")
    void noLogsTest() {
        given()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Get test with logs (GET Books)")
    void withAllLogsTest() {
        given()
                .log().all()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().all()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Get test with chosen logs (GET Books)")
    void withSomeLogsTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Post test (POST Generate user token)")
    void withSomePostTest() {
        given()
                .contentType(JSON)
                .body("{ \"userName\": \"alex\", \"password\": \"W1_#zqwerty\" }")
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Allure listener test with map (POST Generate user token)")
    void withAllureListenerTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        given()
                .contentType(JSON)
                .filter(new AllureRestAssured())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Custom filter test with map (POST Generate user token)")
    void withCustomFilterTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        given()
                .contentType(JSON)
                .filter(customLogFilter().withCustomTemplates())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("AssertJ test with map (POST Generate user token)")
    void withAssertJTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        String response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().asString();
        assertThat(response).contains("\"status\":\"Success\"");
        assertThat(response).contains("\"result\":\"User authorized successfully.\"");
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("AssertJ model test with map (POST Generate user token)")
    void withModelTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");
        AuthorisationResponse response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().as(AuthorisationResponse.class);
        assertThat(response.getStatus()).contains("Success");
        assertThat(response.getResult()).contains("User authorized successfully.");

    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Model test (GET Books)")
    void booksModelTest() {
        Books books =
                given()
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().body()
                        .extract().as(Books.class);
        System.out.println(books);
    }

    @Test
    @Tag("api")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Json schema test (GET Books schema)")
    void booksJsonSchemaTest() {
            given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body(matchesJsonSchemaInClasspath("jsonSchemas/booklist_response.json"));
    }
}
