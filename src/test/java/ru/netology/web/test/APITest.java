package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.APIHelper;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.SQLHelper;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.TransferInfo;

public class APITest {
    static String token;
    static List<DataHelper.CardsInfo> cardsInfo;
    static String firstCard;
    static String secondCard;


    @BeforeAll
    static void setUp() {
        val authInfo = DataHelper.getValidUserAuthInfo();
        token = APIHelper.getToken(authInfo);
        cardsInfo = SQLHelper.getCardsInfo(authInfo.getId());
        firstCard = cardsInfo.get(0).getNumber();
        secondCard = cardsInfo.get(1).getNumber();
    }

    @Test
    void shouldTransfer5ToAnotherCard() {
        val balanceBeforeTransaction = DataHelper.getCardBalance(token, firstCard);
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(), 5);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = DataHelper.getCardBalance(token, firstCard);
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + 5);
    }

    @Test
    void shouldTransferSumWithKopecksToAnotherCard() {
        val balanceBeforeTransaction = DataHelper.getCardBalance(token, firstCard);
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(), 50.5);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = DataHelper.getCardBalance(token, firstCard);
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + 50.5);
    }

    @Test
    void shouldTransferMaxToAnotherCard() {
        val balanceBeforeTransaction = DataHelper.getCardBalance(token, firstCard);
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(),
                balanceBeforeTransaction);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransaction = DataHelper.getCardBalance(token, firstCard);
        assertEquals(balanceBeforeTransaction, balanceAfterTransaction + balanceBeforeTransaction);
    }

    @Test
    void shouldNotTransferMinus1000ToAnotherCard() {
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(), -1000);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransfer0ToAnotherCard() {
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(), 1);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransferOverMaxToAnotherCard() {
        val balanceBeforeTransaction = DataHelper.getCardBalance(token, firstCard);
        val transferInfo = new TransferInfo(firstCard, DataHelper.getFakerCardNumber(),
                balanceBeforeTransaction + 1);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldTransfer5ToMyCard() {
        val balanceBeforeTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceBeforeTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        val transferInfo = new TransferInfo(firstCard, secondCard, 5);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceAfterTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        assertEquals(balanceBeforeTransactionCardFrom, balanceAfterTransactionCardFrom + 5);
        assertEquals(balanceBeforeTransactionCardTo, balanceAfterTransactionCardTo - 5);
    }

    @Test
    void shouldTransfer5ToMy2ndCard() {
        val balanceBeforeTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceBeforeTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        val transferInfo = new TransferInfo(firstCard, secondCard, 5);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceAfterTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        assertEquals(balanceBeforeTransactionCardFrom, balanceAfterTransactionCardFrom + 5);
        assertEquals(balanceBeforeTransactionCardTo, balanceAfterTransactionCardTo - 5);
    }

    @Test
    void shouldTransferMaxToMyCard() {
        val balanceBeforeTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceBeforeTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        val transferInfo = new TransferInfo(firstCard, secondCard,
                balanceBeforeTransactionCardFrom);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceAfterTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        assertEquals(balanceBeforeTransactionCardFrom,
                balanceAfterTransactionCardFrom + balanceBeforeTransactionCardFrom);
        assertEquals(balanceBeforeTransactionCardTo,
                balanceAfterTransactionCardTo - balanceBeforeTransactionCardFrom);
    }

    @Test
    void shouldTransferMaxToMy2ndCard() {
        val balanceBeforeTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceBeforeTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        val transferInfo = new TransferInfo(firstCard, secondCard,
                balanceBeforeTransactionCardFrom);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(200);
        val balanceAfterTransactionCardFrom = DataHelper.getCardBalance(token, firstCard);
        val balanceAfterTransactionCardTo = DataHelper.getCardBalance(token, secondCard);
        assertEquals(balanceBeforeTransactionCardFrom,
                balanceAfterTransactionCardFrom + balanceBeforeTransactionCardFrom);
        assertEquals(balanceBeforeTransactionCardTo,
                balanceAfterTransactionCardTo - balanceBeforeTransactionCardFrom);
    }

    @Test
    void shouldNotTransferMinus1000ToMyCard() {
        val transferInfo = new TransferInfo(firstCard, secondCard, -1000);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransfer0ToMyCard() {
        val transferInfo = new TransferInfo(firstCard, secondCard, 0);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransferOverMaxToMyCard() {
        val balanceBeforeTransaction = DataHelper.getCardBalance(token, firstCard);
        val transferInfo = new TransferInfo(firstCard, secondCard,
                balanceBeforeTransaction + 1);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @Test
    void shouldNotTransferFromToOnceCard() {
        val transferInfo = new TransferInfo(firstCard, firstCard, 5);
        APIHelper.transferResponse(token, transferInfo).then().statusCode(400);
    }

    @AfterEach
    void restoreBalance() {
        SQLHelper.restoreBalance(new String[] {firstCard, secondCard});
    }

    @AfterAll
    static void cleanData() {
        SQLHelper.clean();
    }
}
