package tests;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static helpers.AttachmentsHelper.*;
import static helpers.DriverHelper.*;

@ExtendWith({AllureJunit5.class})
public class TestBase {
    public static final String BASEURL = "https://job.playrix.com/";

    @BeforeAll
    static void setUp() {
        configureDriver();
        RestAssured.baseURI = TestData.getApiUrl();
        Configuration.baseUrl = TestData.getWebUrl();
    }

    @AfterEach
    public void addAttachments() {
        String sessionId = getSessionId();

        attachScreenshot("Last screenshot");
        attachPageSource();
//        attachNetwork(); // todo
        attachAsText("Browser console logs", getConsoleLogs());

        closeWebDriver();
        if (isVideoOn()) attachVideo(sessionId);
    }
}