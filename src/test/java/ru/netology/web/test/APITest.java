package ru.netology.web.test;

import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.api.APIHelper;
import ru.netology.web.data.DataHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITest {
    static RequestSpecification requestSpec;
    static String token;

    @BeforeAll
    static void setUp() {
        requestSpec = APIHelper.getRequestSpec();
        token = APIHelper.getToken(requestSpec);
    }

    @Test
    void shouldTransfer5ToAnotherCard() {
        val balanceBeforeTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(5);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + 5);
    }

    @Test
    void shouldTransferSumWithKopecksToAnotherCard() {
        val balanceBeforeTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(50.5);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + 50.5);
    }

    @Test
    void shouldTransferMaxToAnotherCard() {
        val balanceBeforeTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(balanceBeforeTransaction);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + balanceBeforeTransaction);
    }

    @Test
    void shouldNotTransferMinus1000ToAnotherCard() {
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(-1000);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransfer0ToAnotherCard() {
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(0);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransferOverMaxToAnotherCard() {
        val balanceBeforeTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0008(
                balanceBeforeTransaction + 1);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldTransfer5ToMyCard() {
        val balanceBeforeTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0001");
        val balanceBeforeTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0002");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0002(5);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0001");
        val balanceAfterTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0002");
        assertEquals(balanceBeforeTransactionCardFrom, balanceAfterTransactionCardFrom + 5);
        assertEquals(balanceBeforeTransactionCardTo, balanceAfterTransactionCardTo - 5);
    }

    @Test
    void shouldTransfer5ToMy2ndCard() {
        val balanceBeforeTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0002");
        val balanceBeforeTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0002To0001(5);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0002");
        val balanceAfterTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0001");
        assertEquals(balanceBeforeTransactionCardFrom, balanceAfterTransactionCardFrom + 5);
        assertEquals(balanceBeforeTransactionCardTo, balanceAfterTransactionCardTo - 5);
    }

    @Test
    void shouldTransferMaxToMyCard() {
        val balanceBeforeTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0001");
        val balanceBeforeTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0002");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0002(balanceBeforeTransactionCardFrom);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0001");
        val balanceAfterTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0002");
        assertEquals(balanceBeforeTransactionCardFrom,
                balanceAfterTransactionCardFrom + balanceBeforeTransactionCardFrom);
        assertEquals(balanceBeforeTransactionCardTo,
                balanceAfterTransactionCardTo - balanceBeforeTransactionCardFrom);
    }

    @Test
    void shouldTransferMaxToMy2ndCard() {
        val balanceBeforeTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0002");
        val balanceBeforeTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0002To0001(balanceBeforeTransactionCardFrom);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = APIHelper.getCardBalance(requestSpec, token,"0002");
        val balanceAfterTransactionCardTo = APIHelper.getCardBalance(requestSpec, token,"0001");
        assertEquals(balanceBeforeTransactionCardFrom,
                balanceAfterTransactionCardFrom + balanceBeforeTransactionCardFrom);
        assertEquals(balanceBeforeTransactionCardTo,
                balanceAfterTransactionCardTo - balanceBeforeTransactionCardFrom);
    }

    @Test
    void shouldNotTransferMinus1000ToMyCard() {
        val transferInfo = DataHelper.getTransferInfoFrom0001To0002(-1000);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransfer0ToMyCard() {
        val transferInfo = DataHelper.getTransferInfoFrom0001To0002(0);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransferOverMaxToMyCard() {
        val balanceBeforeTransaction = APIHelper.getCardBalance(requestSpec, token,"0001");
        val transferInfo = DataHelper.getTransferInfoFrom0001To0002(
                balanceBeforeTransaction + 1);
        APIHelper.transferResponse(requestSpec, token, transferInfo).then().statusCode(400);
    }

    @AfterEach
    void restoreBalance() {
        DataHelper.restoreBalance();
    }
    @AfterAll
    static void cleanData() {
        DataHelper.clean();
    }
}
