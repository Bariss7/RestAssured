import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.*;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {

        given()


                // hazırlık işlemlerini yapacağız (token,send body, parametreler)
                .when()
                // linki ve metodu veriyoruz

                .then();
        // assertion ve verileri ele alma extract

    }

    @Test
    public void statusCodeTest() {

        given()


                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // bütün responsu gösterir log.all()
                .statusCode(200) // status kontrolü
        ;

    }

    @Test
    public void contentTypeTest() {

        given()


                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // bütün responsu gösterir log.all()
                .statusCode(200) // status kontrolü
                .contentType(ContentType.JSON) // hatalı durum kontrolünü yapalım
        ;

    }

    @Test
    public void checkStateInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States"))
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void BodyJsonPathTest2() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California"))
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void BodyJsonPathTest3() {

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places. 'place name'", hasItem("Çaputcu Köyü"))//bir index verilmeezse dizinin bütün elemanları verir
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void BodyArrayHasSizeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1)) //verilen path deki listin size kontrolü
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void combiningTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))//verilen path deki listin size kontrolü
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country", "us")
                .pathParam("ZipKod", 90210)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                .then()
                .log().body()

                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void pathParamTest2() {
        //90210 dan 90250 kadar test sonuçlarında places in size nın hepsinde 1 geldiğini test ediniz
        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParam("Country", "us")
                    .pathParam("ZipKod", i)
                    .log().uri()

                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                    .then()
                    .log().body()
                    .body("places", hasSize(1))
                    .statusCode(200) // status kontrolü
            ;
        }
    }
    @Test
    public void queryParamTest() {

        given()
                .param("page", 1)
                .log().uri()

                .when()
                .get("http://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .statusCode(200) // status kontrolü
        ;
    }
    @Test
    public void queryParamTest2() {
        for (int pageNo = 1; pageNo <=10; pageNo++) {
            given()
                    .param("page", pageNo)
                    .log().uri()

                    .when()
                    .get("http://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(pageNo))
                    .statusCode(200) // status kontrolü
            ;
        }
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;
    @BeforeClass
    void Setup(){
        baseURI="http://gorest.co.in/public/v1";

        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }
    @Test
    public void requestResponseSpecification() {

        given()
                .param("page", 1)
                .spec(requestSpecs)

                .when()
                .get("/users")

                .then()
                .body("meta.pagination.page", equalTo(1))
                .spec(responseSpecs)
        ;
    }
    //Json exract
    @Test
    public void extractingJsonPath() {
        String placeName=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'")
                //extract metodu ile given ile başlayan satır, bir değer döndürür hale geldi en sonda extract olmalı
        ;

        System.out.println("placeName = " + placeName);
    }
    @Test
    public void extractingJsonPathInt() {
        int limit=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit")
                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }
    @Test
    public void extractingJsonPathInt2() {
        int id=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data[2].id")
                ;
        System.out.println("id = " + id);
    }
    @Test
    public void extractingJsonPathIntList() {
        List<Integer> idler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.id")// data daki bütün idler list şeklinde verir.
                ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(3045));
    }
    @Test
    public void extractingJsonPathStringList() {
        List<String> isimler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.name")// data daki bütün idler list şeklinde verir.
                ;
        System.out.println("isimler = " + isimler);
        Assert.assertTrue(isimler.contains("Datta Achari"));
    }
    @Test
    public void extractingJsonPathResponseAll() {
        Response response=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().response()//bütün body alındı
                ;
        List<Integer> idler=response.path("data.id");
        List<String> isimler=response.path("data.name");
        int limit=response.path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        System.out.println("isimler = " + isimler);
        System.out.println("idler = " + idler);
    }
    @Test
    public void extractingJsonPOJO() {// POJO: json objecti
        Location yer=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .extract().as(Location.class)//location şablonu
        ;
        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlacename() = " +
                yer.getPlaces().get(0).getPlacename());


    }



}
