package com.nemiliano.qa.demo.data;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.datafaker.Faker;

/**
 * Builder de datos generados (Datafaker). Cada llamada produce datos nuevos y únicos, así los tests
 * no dependen de registros que dejó otro test.
 */
public final class BookingBuilder {

  private static final Faker FAKER = new Faker();

  private String firstname = FAKER.name().firstName();
  private String lastname = FAKER.name().lastName();
  private int totalprice = FAKER.number().numberBetween(100, 1000);
  private boolean depositpaid = true;

  public static BookingBuilder aBooking() {
    return new BookingBuilder();
  }

  public BookingBuilder withTotalPrice(int totalprice) {
    this.totalprice = totalprice;
    return this;
  }

  public String firstname() {
    return firstname;
  }

  /** Cuerpo para restful-booker. */
  public Map<String, Object> asRestfulBookerBody() {
    Map<String, Object> dates = new LinkedHashMap<>();
    dates.put("checkin", LocalDate.now().plusDays(7).toString());
    dates.put("checkout", LocalDate.now().plusDays(10).toString());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("firstname", firstname);
    body.put("lastname", lastname);
    body.put("totalprice", totalprice);
    body.put("depositpaid", depositpaid);
    body.put("bookingdates", dates);
    body.put("additionalneeds", "Desayuno");
    return body;
  }

  /** Nombre de huésped único para la app local (evita choques entre tests en paralelo). */
  public static String uniqueGuest() {
    return FAKER.name().firstName() + "-" + UUID.randomUUID().toString().substring(0, 8);
  }
}
