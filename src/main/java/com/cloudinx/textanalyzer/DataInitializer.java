package com.cloudinx.textanalyzer;

import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataInitializer {

  private final static Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

  private PgPool client;

  public DataInitializer(PgPool client) {
    this.client = client;
  }

  public static DataInitializer create(PgPool client) {
    return new DataInitializer(client);
  }

  public void run() {
    LOGGER.log(Level.INFO, "Data initialization is starting...");

    List<String> values = new ArrayList<>();

    InputStream inputStream = getClass().getResourceAsStream("/words.txt");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      // Read lines from the file
      reader.lines().forEach(line -> {
        List.of(line.split(" ")).stream().forEach(word -> {
          values.add("('" + word + "')");
        });
      });
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }

    String insertQuery = "INSERT INTO words (value) VALUES ";
    String valuesString = String.join(", ", values);
    String finalQuery = insertQuery + valuesString;

    client
      .withTransaction(
        conn -> conn.query(finalQuery)
          .execute()
      .onSuccess(data -> StreamSupport.stream(data.spliterator(), true)
        .forEach(row -> LOGGER.log(Level.INFO, "Number of rows inserted", values.size()))
      )
      .onComplete(
        r -> {
          //client.close(); will block the application.
          LOGGER.info("Data initialization is done...");
        }
      )
      .onFailure(
        throwable -> LOGGER.warning("Data initialization is failed:" + throwable.getMessage())
      ));
  }
}
