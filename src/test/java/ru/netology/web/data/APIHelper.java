package ru.netology.web.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class APIHelper {
    private APIHelper() {
    }

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(JSON)
                .setContentType(JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static String getToken(DataHelper.AuthInfo authInfo) {
        given()
                .spec(getRequestSpec())
                .body(authInfo)
                .when()
                .post("api/auth")
                .then()
                .statusCode(200);
        return given()
                .spec(getRequestSpec())
                .body(DataHelper.getVerificationInfo(authInfo))
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static DataHelper.CardsInfo[] getCardsInfo(String token) {
        return given()
                .spec(getRequestSpec())
                .when()
                .auth().oauth2(token)
                .get("/api/cards/")
                .then()
                .statusCode(200)
                .extract().body().as(DataHelper.CardsInfo[].class);
    }

    public static Response transferResponse(String token, DataHelper.TransferInfo transferInfo) {
        return given()
                .spec(getRequestSpec())
                .body(transferInfo)
                .when()
                .auth().oauth2(token)
                .post("/api/transfer");
    }
}
