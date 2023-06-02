package com.cloudinx.textanalyzer;

import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  private final static Logger LOGGER = Logger.getLogger(TestMainVerticle.class.getName());
  HttpClient client;

  @BeforeEach
  void setup(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    HttpClientOptions options = new HttpClientOptions()
      .setDefaultPort(8888);
    this.client = vertx.createHttpClient(options);
  }

  @RepeatedTest(5)
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @DisplayName("Health")
  void testAnalyze(Vertx vertx, VertxTestContext testContext) {
    client.request(HttpMethod.GET, "/health")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(
        testContext.succeeding(
          buffer -> testContext.verify(
            () -> {
              assertThat(buffer.toString()).isEqualTo("Text analyzer is up and running");
              testContext.completeNow();
            }
          )
        )
      );
  }
}
