package ru.netology.web.test;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

class LogInTest {
    private static String fakeUsername;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        val faker = new Faker();
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )

        ) {
            fakeUsername = faker.name().username();
            runner.update(conn, dataSQL,
                    "1", fakeUsername,
                    "$2a$10$4rFXqbkO3iu6HbRGvdUI2uIcaqg2U3SW.FfrHBQP6P5ewL1xw4Iki");
        }
    }

    @SneakyThrows
    @Test
    void shouldLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @SneakyThrows
    @Test
    void shouldLogInNewUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo(fakeUsername);
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @SneakyThrows
    @Test
    void shouldNotLogInExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.invalidLogin(authInfo).shouldHaveErrorMassage();
    }

    @SneakyThrows
    @Test
    void shouldBlockExistUser() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("petya");
        loginPage.invalidLogin(authInfo).shouldHaveErrorMassage();
        loginPage.invalidLogin(authInfo).shouldHaveErrorMassage();
        loginPage.invalidLogin(authInfo).shouldHaveErrorMassage();
        loginPage.invalidLogin(authInfo).shouldHaveBlockMassage();
    }

    @SneakyThrows
    @Test
    void shouldBlockExistUserByVerificationCode() {
        val loginPage = open("http://localhost:9999", LoginPage.class);
        val authInfo = DataHelper.getAuthInfo("vasya");
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getInvalidVerificationCode();
        verificationPage.invalidVerify(verificationCode).shouldHaveInvalidCodeMessage();
        verificationPage.invalidVerify(verificationCode).shouldHaveInvalidCodeMessage();
        verificationPage.invalidVerify(verificationCode).shouldHaveInvalidCodeMessage();
        verificationPage.invalidVerify(verificationCode).shouldHaveBlockMessage();
    }

    @SneakyThrows
    @AfterAll
    static void setup() {
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            val runner = new ScriptRunner(conn);
            runner.runScript(new FileReader("./src/test/resources/schema.sql"));
        }
    }
}

