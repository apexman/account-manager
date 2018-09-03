package com.maksimov.moneyManager;

import com.maksimov.accountManager.AccountManager;
import com.maksimov.accountManager.controller.AccountController;
import com.maksimov.accountManager.dto.AccountTO;
import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.model.Client;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AccountManager.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountManagerTests {
    private Logger logger = LoggerFactory.getLogger(AccountManagerTests.class);

    @LocalServerPort
    private int port;

    @Before
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void saysHello() {
        when()
            .get("/api/account/hello")
            .then()
                .statusCode(SC_OK)
            .assertThat()
            .body(is(equalTo(AccountController.HELLO_TEXT)));
    }

    @Test
    public void fetchAllAccounts() {
        ArrayList<AccountTO> list = (ArrayList<AccountTO>) given()
                .get("/api/account")
                .then()
                .statusCode(is(SC_OK))
                .extract()
                .body().as(ArrayList.class);

        System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    public void addNewAccountAndRetrieveItBack() {
        Long clientId = 1L;
        Client client =  new Client("myx", "myx");
        client.setId(clientId);

        String norbertSiegmundName = "Norbert Siegmund";
        Account norbertSiegmundAcc = new Account(norbertSiegmundName, client);

        AccountTO account =
            given()
                .queryParam("name", norbertSiegmundName)
                .queryParam("balance", BigDecimal.ONE)
                .queryParam("clientId", clientId)
                .when()
                .post("/api/account/")
                .then()
                    .statusCode(is(SC_OK))
                .extract()
                    .body().as(AccountTO.class);

        AccountTO responseAccount =
            given()
                .pathParam("id", account.getId())
                .when()
                .get("/api/account/{id}")
                .then()
                    .statusCode(SC_OK)
                .assertThat()
                    .extract().as(AccountTO.class);

        // Did Norbert account come back?
        assertThat(responseAccount.getName(), is(norbertSiegmundName));
        assertThat(responseAccount.getBalance(), closeTo(BigDecimal.ONE, BigDecimal.ZERO));
        assertThat(responseAccount.getClientId(), is(clientId));
    }

    @Test
    public void deleteAccount(){
        given()
                .pathParam("id", "TODELETE")
                .when()
                .delete("/api/account/{id}")
                .then()
                .statusCode(SC_OK);
    }

    @Test
    public void deposit(){
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        BigDecimal currentBalance =
            given()
                .pathParam("id", id)
                .when()
                .get("/api/account/{id}")
                .then()
                    .statusCode(is(SC_OK))
                .extract()
                    .body().as(AccountTO.class).getBalance();

        AccountTO account =
            given()
                .pathParam("id", id)
                .queryParam("deposit", BigDecimal.valueOf(2))
                .when()
                .post("/api/account/deposit/{id}")
                .then()
                    .statusCode(is(SC_OK))
                .extract()
                    .body().as(AccountTO.class);

        assertThat(account.getBalance(), closeTo(currentBalance.add(BigDecimal.valueOf(2)), BigDecimal.ZERO));
    }

    @Test
    public void depositToNonexistentAccount(){
        given()
            .pathParam("id", "_NONEXISTENT_")
            .queryParam("deposit", BigDecimal.valueOf(2))
            .when()
            .post("/api/account/deposit/{id}")
            .then()
            .statusCode(is(HttpStatus.SC_NOT_FOUND));

    }

    @Test
    public void withdraw(){
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        BigDecimal currentBalance =
            given()
                .pathParam("id", id)
                .when()
                .get("/api/account/{id}")
                .then()
                    .statusCode(is(SC_OK))
                .extract()
                    .body().as(AccountTO.class).getBalance();

        AccountTO account =
            given()
                .pathParam("id", id)
                .queryParam("withdrawn", BigDecimal.valueOf(7))
                .when()
                .post("/api/account/withdraw/{id}")
                .then()
                    .statusCode(is(SC_OK))
                .extract()
                    .body().as(AccountTO.class);

        assertThat(account.getBalance(), closeTo(currentBalance.subtract(BigDecimal.valueOf(7)), BigDecimal.ZERO));
    }

    @Test
    public void withdrawFromNonexistentAccount() {
        given()
                .pathParam("id", "_NONEXISTENT_")
                .queryParam("withdrawn", BigDecimal.valueOf(7))
                .when()
                .post("/api/account/withdraw/{id}")
                .then()
                .statusCode(is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void transferOk(){
        String accId1 = "165d4252b8f645f0b66c1fc7f727bb4a";
        String accId2 = "0b66c1fc7f727bb4a165d4252b8f645f";

        given()
                .queryParam("idFrom", accId1)
                .queryParam("idWhere", accId2)
                .queryParam("money", BigDecimal.valueOf(5))
                .when()
                .post("/api/account/transfer")
                .then()
                .statusCode(is(SC_OK));
    }

    @Test
    public void transferConcurrentlyToSameAccount(){
        ExecutorService es = Executors.newFixedThreadPool(10);

        try {
            for (int i = 0; i < 1; i++) {
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
                es.execute(this::transferOk);
            }

            es.shutdown();
            es.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void transferMoreThanHave(){
        String accId1 = "165d4252b8f645f0b66c1fc7f727bb4a";
        String accId2 = "0b66c1fc7f727bb4a165d4252b8f645f";

        given()
                .queryParam("idFrom", accId1)
                .queryParam("idWhere", accId2)
                .queryParam("money", BigDecimal.valueOf(5000))
                .when()
                .post("/api/account/transfer")
                .then()
                .statusCode(is(HttpStatus.SC_NOT_ACCEPTABLE));
    }

    @Test
    public void transferFrom1To2AndViceVersaConcurrently() {
        ExecutorService es = Executors.newFixedThreadPool(10);

        try {
            for (int i = 0; i < 1; i++) {
                es.execute(this::transferFrom1To2);
                es.execute(this::transferFrom2To1);

                es.execute(this::transferFrom1To2);
                es.execute(this::transferFrom2To1);

                es.execute(this::transferFrom1To2);
                es.execute(this::transferFrom2To1);

                es.execute(this::transferFrom1To2);
                es.execute(this::transferFrom2To1);

                es.execute(this::transferFrom1To2);
                es.execute(this::transferFrom2To1);
            }

            es.shutdown();
            es.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void transferFrom1To2(){
        String accId1 = "165d4252b8f645f0b66c1fc7f727bb4a";
        String accId2 = "0b66c1fc7f727bb4a165d4252b8f645f";

        try {
            Thread.sleep(0);

            logger.debug("from 1 to 2");

            given()
                    .queryParam("idFrom", accId1)
                    .queryParam("idWhere", accId2)
                    .queryParam("money", BigDecimal.valueOf(1))
                    .when()
                    .post("/api/account/transfer");

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void transferFrom2To1(){
        String accId1 = "165d4252b8f645f0b66c1fc7f727bb4a";
        String accId2 = "0b66c1fc7f727bb4a165d4252b8f645f";

        try {
            Thread.sleep(10);

            logger.debug("from 2 to 1");

            given()
                    .queryParam("idFrom", accId2)
                    .queryParam("idWhere", accId1)
                    .queryParam("money", BigDecimal.valueOf(2))
                    .when()
                    .post("/api/account/transfer");

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void depositAndWithdrawConcurrently() {
        ExecutorService es = Executors.newFixedThreadPool(3);

        try {
//            persistArticle();
            es.execute(this::depositAccount);
            //simulating other user by using different thread
            es.execute(this::withdrawAccount);
            es.shutdown();
            es.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void depositAccount() {
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        BigDecimal currentBalance =
                given()
                        .pathParam("id", id)
                        .when()
                        .post("/api/account/{id}")
                        .then()
                        .statusCode(is(SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();

        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("deposit", BigDecimal.valueOf(2))
                        .when()
                        .post("/api/account/deposit/{id}")
                        .then()
                        .statusCode(is(SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.add(BigDecimal.valueOf(2)), BigDecimal.ZERO));
    }

    private void withdrawAccount() {
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        BigDecimal currentBalance =
                given()
                        .pathParam("id", id)
                        .when()
                        .post("/api/account/{id}")
                        .then()
                        .statusCode(is(SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();


        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("withdrawn", BigDecimal.valueOf(4))
                        .when()
                        .post("/api/account/withdraw/{id}")
                        .then()
                        .statusCode(is(SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.subtract(BigDecimal.valueOf(4)), BigDecimal.ZERO));
    }

}
