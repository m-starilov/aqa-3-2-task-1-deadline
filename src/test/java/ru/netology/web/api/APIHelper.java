package ru.netology.web.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import ru.netology.web.data.DataHelper;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class APIHelper {
    private APIHelper() {

    }

    private static double balance;

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(JSON)
                .setContentType(JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static String getToken(RequestSpecification requestSpec) {
        given()
                .spec(requestSpec)
                .body(DataHelper.getAuthInfo("vasya"))
                .when()
                .post("api/auth")
                .then()
                .statusCode(200);
        return  given()
                .spec(requestSpec)
                .body(DataHelper.getVerificationInfo("vasya"))
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static double getCardBalance(RequestSpecification requestSpec, String token, String last4digit) {
        val cardsInfo = given()
                .spec(requestSpec)
                .when()
                .auth().oauth2(token)
                .get("/api/cards/")
                .then()
                .statusCode(200)
                .extract().body().as(DataHelper.CardsInfo[].class);
        for (DataHelper.CardsInfo array:cardsInfo) {
            if(array.getNumber().contains(last4digit)) {
                balance = array.getBalance();
            }
        }
        return balance;
    }

    public static Response transferResponse(RequestSpecification requestSpec,
                                    String token,
                                    DataHelper.TransferInfo transferInfo) {
        return given()
                .spec(requestSpec)
                .body(transferInfo)
                .when()
                .auth().oauth2(token)
                .post("/api/transfer");
    }
}
