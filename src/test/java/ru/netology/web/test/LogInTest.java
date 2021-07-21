package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.SQLHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

class LogInTest {
    private static LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    void shouldLogInExistUser() {
        val authInfo = DataHelper.getValidUserAuthInfo();
        val verificationPage = loginPage.login(authInfo);
        val verificationCode = DataHelper.getVerificationCode(authInfo);
        verificationPage.verify(verificationCode);
    }

    @Test
    void shouldLogInNewUser() {
        val authInfo = DataHelper.getFakeUserAuthInfo();
        val verificationPage = loginPage.login(authInfo);
        val verificationCode = DataHelper.getVerificationCode(authInfo);
        verificationPage.verify(verificationCode);
    }

    @Test
    void shouldNotLogInExistUser() {
        val authInfo = DataHelper.getInvalidUserAuthInfo();
        loginPage.loginWithInvalidData(authInfo);
        loginPage.shouldHaveErrorMassage();
    }

    @Test
    void shouldBlockExistUser() {
        val authInfo = DataHelper.getValidUserAuthInfo();
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
        val authInfo = DataHelper.getValidUserAuthInfo();
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
        SQLHelper.clean();
    }
}

