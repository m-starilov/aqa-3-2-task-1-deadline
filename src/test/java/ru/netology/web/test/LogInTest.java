package ru.netology.web.test;

import com.github.javafaker.Faker;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

class LogInTest {
    private static String fakeUsername;

    @BeforeAll
    static void addNewUser() {
        val faker = new Faker();
        fakeUsername = faker.name().username();
        DataHelper.addNewUser(fakeUsername);
    }

    @Test
    void shouldLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        loginPage.login(authInfo);
        val verificationPage = loginPage.success();
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.verify(verificationCode);
        verificationPage.success();
    }

    @Test
    void shouldLogInNewUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo(fakeUsername);
        loginPage.login(authInfo);
        val verificationPage = loginPage.success();
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.verify(verificationCode);
        verificationPage.success();
    }

    @Test
    void shouldNotLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.login(authInfo);
        loginPage.shouldHaveErrorMassage();
    }

    @Test
    void shouldBlockExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.login(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.login(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.login(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.login(authInfo);
        loginPage.shouldHaveBlockMassage();
    }

    @Test
    void shouldBlockExistUserByVerificationCode() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        loginPage.login(authInfo);
        val verificationPage = loginPage.success();
        val verificationCode = DataHelper.getInvalidVerificationCode();
        verificationPage.verify(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verify(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verify(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verify(verificationCode);
        verificationPage.shouldHaveBlockMessage();
    }

    @AfterAll
    static void cleanData() {
        DataHelper.clean();
    }
}

