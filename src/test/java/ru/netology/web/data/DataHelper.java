package ru.netology.web.data;

import com.github.javafaker.Faker;
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

    @Value
    public static class VerificationInfo {
        String login;
        String code;
    }

    @Value
    public static class TransferInfo {
        String from;
        String to;
        double amount;
    }

    @Value
    public static class CardsInfo {
        String id;
        String number;
        int balance;
    }

    public static String getFakerUsername() {
        val faker = new Faker();
        return faker.name().username();
    }

    public static VerificationInfo getVerificationInfo(String login) {
        val authInfo = getAuthInfo(login);
        val verificationCode = getVerificationCodeFor(authInfo);
        return new VerificationInfo(authInfo.login, verificationCode.code);
    }

    public static VerificationCode getInvalidVerificationCode() {
        return new VerificationCode("00");
    }

    public static TransferInfo getTransferInfoFrom0001To0008(double amount) {
        return new TransferInfo("5559 0000 0000 0001","5559 0000 0000 0008", amount);
    }

    public static TransferInfo getTransferInfoFrom0001To0002(double amount) {
        return new TransferInfo("5559 0000 0000 0001","5559 0000 0000 0002", amount);
    }

    public static TransferInfo getTransferInfoFrom0002To0001(double amount) {
        return new TransferInfo("5559 0000 0000 0002","5559 0000 0000 0001", amount);
    }

    @SneakyThrows
    public static AuthInfo getAuthInfo(String login) {
        User userInfo;
        val userSQL = "SELECT id, login FROM users WHERE login = ?;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            userInfo = runner.query(conn, userSQL, new BeanHandler<>(User.class), login);
        }
        return new AuthInfo(userInfo.getId(), userInfo.getLogin(), "qwerty123");
    }

    @SneakyThrows
    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        VerificationCode verificationCode;
        val authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC LIMIT 1;";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            verificationCode = new VerificationCode(runner.query(conn, authCodeSQL, new ScalarHandler<>(), authInfo.id));
        }
        return verificationCode;
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
    public static void restoreBalance() {
        val runner = new QueryRunner();
        val dataSQL = "UPDATE cards SET `balance_in_kopecks` = 1000000  " +
                "WHERE `number` IN ('5559 0000 0000 0001', '5559 0000 0000 0002')";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            runner.update(conn, dataSQL);
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