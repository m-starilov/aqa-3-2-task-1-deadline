package ru.netology.web.data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.FileReader;
import java.sql.DriverManager;
import java.util.List;

public class SQLHelper {
    private SQLHelper() {
    }

    @SneakyThrows
    public static DataHelper.AuthInfo getAuthInfo(String login) {
        DataHelper.User userInfo;
        val userSQL = "SELECT id, login FROM users WHERE login = ?;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            userInfo = runner.query(conn, userSQL, new BeanHandler<>(DataHelper.User.class), login);
        }
        return new DataHelper.AuthInfo(userInfo.getId(), userInfo.getLogin(), "qwerty123");
    }

    @SneakyThrows
    public static DataHelper.VerificationInfo getVerificationInfo(DataHelper.AuthInfo authInfo) {
        String verificationCode;
        val authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC LIMIT 1;";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            verificationCode = runner.query(conn, authCodeSQL, new ScalarHandler<>(), authInfo.getId());
        }
        return new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode);
    }

    @SneakyThrows
    public static List<DataHelper.CardsInfo> getCardsInfo(String userID) {
        List<DataHelper.CardsInfo> cardsInfo;
        val userSQL = "SELECT id, number, balance_in_kopecks FROM cards WHERE user_id = ? ORDER BY number;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            cardsInfo = runner.query(conn, userSQL, new BeanListHandler<>(DataHelper.CardsInfo.class), userID);
        }
        return cardsInfo;
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
