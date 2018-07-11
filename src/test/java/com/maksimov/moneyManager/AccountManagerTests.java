package com.maksimov.moneyManager;

import com.maksimov.accountManager.AccountManager;
import com.maksimov.accountManager.account.Account;
import com.maksimov.accountManager.account.AccountController;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AccountManager.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountManagerTests {

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
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body(is(equalTo(AccountController.HELLO_TEXT)));
    }

    @Test
    public void addNewAccountAndRetrieveItBack() {
        String norbertSiegmundName = "Norbert Siegmund";
        Account norbertSiegmundAcc = new Account();
        norbertSiegmundAcc.setName(norbertSiegmundName);

        Account account =
                given()
                        .queryParam("name", norbertSiegmundName)
                        .queryParam("balance", BigDecimal.ONE)
                        .when()
                        .post("/api/account/update")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class);

        Account responseAccount =
                given()
                        .pathParam("id", account.getId())
                        .when()
                        .get("/api/account/{id}")
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .assertThat()
                        .extract().as(Account.class);

        // Did Norbert account come back?
        assertThat(responseAccount.getName(), is(norbertSiegmundName));
        assertThat(responseAccount.getBalance(), closeTo(BigDecimal.ONE, BigDecimal.ZERO));
    }

    @Test
    public void deposit(){
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        BigDecimal currentBalance =
                given()
                        .pathParam("id", id)
                        .when()
                        .post("/api/account/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();

        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("deposit", BigDecimal.valueOf(2))
                        .when()
                        .post("/api/account/deposit/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.add(BigDecimal.valueOf(2)), BigDecimal.ZERO));
    }

    @Test
    public void withdraw(){
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        BigDecimal currentBalance =
                given()
                        .pathParam("id", id)
                        .when()
                        .post("/api/account/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();

        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("withdrawn", BigDecimal.valueOf(7))
                        .when()
                        .post("/api/account/withdraw/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.subtract(BigDecimal.valueOf(7)), BigDecimal.ZERO));
    }

    @Test
    public void depositAndWithdrawConcurrently() {
        ExecutorService es = Executors.newFixedThreadPool(3);

        try {
//            persistArticle();
            es.execute(() -> {
                depositAccount();
            });
            es.execute(() -> {
                //simulating other user by using different thread
                withdrawAccount();
            });
            es.shutdown();
            es.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();

        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("deposit", BigDecimal.valueOf(2))
                        .when()
                        .post("/api/account/deposit/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.add(BigDecimal.valueOf(2)), BigDecimal.ZERO));
    }

    private void withdrawAccount() {
        String id = "165d4252b8f645f0b66c1fc7f727bb4a";

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BigDecimal currentBalance =
                given()
                        .pathParam("id", id)
                        .when()
                        .post("/api/account/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class).getBalance();


        Account account =
                given()
                        .pathParam("id", id)
                        .queryParam("withdrawn", BigDecimal.valueOf(4))
                        .when()
                        .post("/api/account/withdraw/{id}")
                        .then()
                        .statusCode(is(HttpStatus.SC_OK))
                        .extract()
                        .body().as(Account.class);

        assertThat(account.getBalance(), closeTo(currentBalance.subtract(BigDecimal.valueOf(4)), BigDecimal.ZERO));
    }

}
