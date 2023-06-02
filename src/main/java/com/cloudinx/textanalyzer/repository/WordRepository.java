package com.cloudinx.textanalyzer.repository;

import com.cloudinx.textanalyzer.model.Word;
import com.cloudinx.textanalyzer.exceptions.WordNotFoundException;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WordRepository {
  private static final Logger LOGGER = Logger.getLogger(WordRepository.class.getName());

  private static Function<Row, Word> MAPPER = (row) ->
    Word.of(
      row.getUUID("id"),
      row.getString("value")
    );
  private final PgPool client;

  private WordRepository(PgPool _client) {
    this.client = _client;
  }

  //factory method
  public static WordRepository create(PgPool client) {
    return new WordRepository(client);
  }

  public Future<List<Word>> findAll() {
    return client.query("SELECT * FROM words ORDER BY id ASC")
      .execute()
      .map(rs -> StreamSupport.stream(rs.spliterator(), false)
        .map(MAPPER)
        .collect(Collectors.toList())
      );
  }

  public Future<Word> findById(UUID id) {
    Objects.requireNonNull(id, "id can not be null");
    return client.preparedQuery("SELECT * FROM words WHERE id=$1").execute(Tuple.of(id))
      .map(RowSet::iterator)
      .map(iterator -> {
          if (iterator.hasNext()) {
            return MAPPER.apply(iterator.next());
          }
          throw new WordNotFoundException(id);
        }
      );
  }

  public Future<Word> findByValue(String value) {
    Objects.requireNonNull(value, "value can not be null");
    return client.preparedQuery("SELECT * FROM words WHERE value=$1").execute(Tuple.of(value))
      .map(RowSet::iterator)
      .map(iterator -> {
          if (iterator.hasNext()) {
            return MAPPER.apply(iterator.next());
          }
          throw new WordNotFoundException(value);
        }
      );
  }

  public Future<Boolean> wordExists(String value) {
    Objects.requireNonNull(value, "value can not be null");
    return client.preparedQuery("SELECT * FROM words WHERE value=$1").execute(Tuple.of(value)).map(rs ->
      rs.rowCount() > 0);
  }

  public Future<UUID> save(Word data) {
    return client.preparedQuery("INSERT INTO words(value) VALUES ($1) RETURNING (id)").execute(Tuple.of(data.getValue()))
      .map(rs -> rs.iterator().next().getUUID("id"));
  }

  public Future<Integer> saveAll(List<Word> data) {
    List<Tuple> tuples = data.stream()
      .map(
        d -> Tuple.of(d.getValue())
      )
      .collect(Collectors.toList());

    return client.preparedQuery("INSERT INTO words (value) VALUES ($1)")
      .executeBatch(tuples)
      .map(SqlResult::rowCount);
  }

  public Future<Integer> deleteAll() {
    return client.query("DELETE FROM words").execute()
      .map(SqlResult::rowCount);
  }

  public Future<Integer> deleteById(UUID id) {
    Objects.requireNonNull(id, "id can not be null");
    return client.preparedQuery("DELETE FROM words WHERE id=$1").execute(Tuple.of(id))
      .map(SqlResult::rowCount);
  }

  public Future<Integer> deleteByValue(String value) {
    Objects.requireNonNull(value, "value can not be null");
    return client.preparedQuery("DELETE FROM words WHERE id=$1").execute(Tuple.of(value))
      .map(SqlResult::rowCount);
  }
}
