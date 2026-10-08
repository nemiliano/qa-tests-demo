package com.nemiliano.qa.demo.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.api.ApiClient;
import com.nemiliano.qa.api.ApiResponse;
import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.demo.data.BookingBuilder;
import com.nemiliano.qa.junit.QaExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** API pública de práctica: https://restful-booker.herokuapp.com */
@Tag("api")
@ExtendWith(QaExtension.class)
class RestfulBookerApiTest {

  // Credenciales PÚBLICAS de la documentación de restful-booker (sitio de práctica).
  // En un sistema real irían en variables de ambiente, nunca en el código.
  private static final Map<String, String> ADMIN =
      Map.of("username", "admin", "password", "password123");

  private ApiClient api;
  private final List<Integer> createdBookings = new ArrayList<>();

  @BeforeEach
  void setUp(QaConfig config) {
    api = ApiClient.fromConfig(config);
  }

  /** Limpieza: cada test borra lo que creó, para ser independiente de los demás. */
  @AfterEach
  void cleanUp() {
    if (createdBookings.isEmpty()) {
      return;
    }
    ApiClient admin = api.withHeader("Cookie", "token=" + token());
    createdBookings.forEach(id -> admin.delete("/booking/" + id));
  }

  @Test
  @Tag("smoke")
  @DisplayName("Crear una reserva devuelve su id y los datos enviados")
  void createBookingReturnsIdAndData() {
    // Arrange
    BookingBuilder booking = BookingBuilder.aBooking();

    // Act
    ApiResponse response = api.post("/booking", booking.asRestfulBookerBody());
    createdBookings.add(response.jsonPath("bookingid"));

    // Assert
    assertThat(response.<String>jsonPath("booking.firstname")).isEqualTo(booking.firstname());
  }

  @Test
  @Tag("regression")
  @DisplayName("Una reserva borrada ya no se puede consultar")
  void deletedBookingIsGone() {
    // Arrange
    int id =
        api.post("/booking", BookingBuilder.aBooking().asRestfulBookerBody()).jsonPath("bookingid");

    // Act
    api.withHeader("Cookie", "token=" + token()).delete("/booking/" + id);

    // Assert
    assertThat(api.get("/booking/" + id).status()).isEqualTo(404);
  }

  private String token() {
    return api.post("/auth", ADMIN).jsonPath("token");
  }
}
