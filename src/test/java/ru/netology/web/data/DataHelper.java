package ru.netology.web.data;

import lombok.Value;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static AuthInfo getAuthInfo(String login) throws SQLException {
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

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) throws SQLException {
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
}