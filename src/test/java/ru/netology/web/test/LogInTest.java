package ru.netology.web.test;

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
        fakeUsername = DataHelper.getFakerUsername();
        DataHelper.addNewUser(fakeUsername);
    }

    @Test
    void shouldLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        val verificationPage = loginPage.login(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.verify(verificationCode);
    }

    @Test
    void shouldLogInNewUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo(fakeUsername);
        val verificationPage = loginPage.login(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.verify(verificationCode);
    }

    @Test
    void shouldNotLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveErrorMassage();
    }

    @Test
    void shouldBlockExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveErrorMassage();
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveBlockMassage();
    }

    @Test
    void shouldBlockExistUserByVerificationCode() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        val verificationPage = loginPage.login(authInfo);
        val verificationCode = DataHelper.getInvalidVerificationCode();
        verificationPage.verifyWithInvalidCode(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verifyWithInvalidCode(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verifyWithInvalidCode(verificationCode);
        verificationPage.shouldHaveInvalidCodeMessage();
        verificationPage.verifyWithInvalidCode(verificationCode);
        verificationPage.shouldHaveBlockMessage();
    }

    @AfterAll
    static void cleanData() {
        DataHelper.clean();
    }
}

