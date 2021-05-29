package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.AuthorisationResponse;
import models.Books;
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

public class BookStoreTests {
    @Test
    @Tag("api")
    void noLogsTest() {
        given()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    @Tag("api")
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
