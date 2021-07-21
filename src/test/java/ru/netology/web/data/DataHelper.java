package ru.netology.web.data;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import lombok.*;
import org.apache.commons.lang3.StringUtils;


public class DataHelper {
    private DataHelper() {
    }

    private static double balance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        String id;
        String login;
        String password;
    }

    @Value
    public static class AuthInfo {
        String id;
        String login;
        String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardsInfo {
        String id;
        String number;
        int balance;
    }

    public static String getFakerUsername() {
        val faker = new Faker();
        return faker.name().username();
    }

    public static AuthInfo getValidUserAuthInfo() {
        return SQLHelper.getAuthInfo("vasya");
    }

    public static AuthInfo getInvalidUserAuthInfo() {
        return SQLHelper.getAuthInfo("petya");
    }

    public static AuthInfo getFakeUserAuthInfo() {
        val fakeUsername = getFakerUsername();
        SQLHelper.addNewUser(fakeUsername);
        return SQLHelper.getAuthInfo(fakeUsername);
    }

    public static VerificationInfo getVerificationInfo(DataHelper.AuthInfo authInfo) {
        return SQLHelper.getVerificationInfo(authInfo);
    }

    public static String getVerificationCode(DataHelper.AuthInfo authInfo) {
        return getVerificationInfo(authInfo).getCode();
    }

    public static String getInvalidVerificationCode() {
        return "00";
    }

    public static String getFakerCardNumber() {
        val faker = new Faker();
        return faker.finance().creditCard(CreditCardType.MASTERCARD).replaceAll("-", " ");
    }

    public static double getCardBalance(String token, String last4digit) {
        val cardsInfo = APIHelper.getCardsInfo(token);
        for (DataHelper.CardsInfo array : cardsInfo) {
            if (array.getNumber().contains(last4digit)) {
                balance = array.getBalance();
            }
        }
        return balance;
    }

    public static String getLast4Digit(String inputString) {
        return StringUtils.right(inputString, 4);
    }
}