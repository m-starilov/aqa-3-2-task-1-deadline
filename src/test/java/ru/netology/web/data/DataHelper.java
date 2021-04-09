package ru.netology.web.data;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.FileReader;
import java.sql.DriverManager;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String id;
        String login;
        String password;
    }

    @Value
    public static class VerificationCode {
        String code;
    }

    public static VerificationCode getInvalidVerificationCode() {
        return new VerificationCode("00000");
    }

    @SneakyThrows
    public static AuthInfo getAuthInfo(String login) {
        User userFromDB;
        val userSQL = "SELECT id, login FROM users WHERE login = ?;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            userFromDB = runner.query(conn, userSQL, new BeanHandler<>(User.class), login);
        }
        return new AuthInfo(userFromDB.getId(), userFromDB.getLogin(), "qwerty123");
    }

    @SneakyThrows
    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        String code;
        val authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC LIMIT 1;";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), authInfo.id);
        }
        return new VerificationCode(code);
    }

    @SneakyThrows
    public static void addNewUser(String username) {
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            runner.update(conn, dataSQL,
                    "1", username,
                    "$2a$10$4rFXqbkO3iu6HbRGvdUI2uIcaqg2U3SW.FfrHBQP6P5ewL1xw4Iki");
        }
    }

    @SneakyThrows
    public static void clean() {
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