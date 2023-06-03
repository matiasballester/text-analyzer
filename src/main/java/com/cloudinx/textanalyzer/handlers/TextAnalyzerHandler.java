package com.cloudinx.textanalyzer.handlers;

import com.cloudinx.textanalyzer.analyzer.ClosestWord;
import com.cloudinx.textanalyzer.command.CreateWordCommand;
import com.cloudinx.textanalyzer.model.Word;
import com.cloudinx.textanalyzer.repository.WordRepository;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TextAnalyzerHandler {
  private static final Logger LOGGER = Logger.getLogger(TextAnalyzerHandler.class.getSimpleName());

  private WordRepository texts;

  private TextAnalyzerHandler(WordRepository _texts) {
    this.texts = _texts;
  }

  public static TextAnalyzerHandler create(WordRepository texts) {
    return new TextAnalyzerHandler(texts);
  }

  public void all(RoutingContext rc) {
    this.texts.findAll()
      .onSuccess(
        data -> rc.response().end(Json.encode(data))
      );
  }

  public void get(RoutingContext rc) {
    Map<String, String> params = rc.pathParams();
    String id = params.get("id");
    this.texts.findById(UUID.fromString(id))
      .onSuccess(
        post -> rc.response().end(Json.encode(post))
      )
      .onFailure(
        throwable -> rc.fail(throwable)
      );

  }

  public void analyze(RoutingContext rc) {
    JsonObject body = rc.getBodyAsJson();
    Instant startTime = Instant.now();
    LOGGER.log(Level.INFO, "request body: {0}", body);

    Word wordToProcess = Word.of(body.mapTo(CreateWordCommand.class).getText());

    this.texts.findAll().onSuccess(words -> {
      this.save(rc);
      List<String> existingWords = words.stream().map(p -> p.getValue()).collect(Collectors.toList());
      JsonObject jsonObject = new JsonObject()
        .put("value", ClosestWord.find(wordToProcess.getValue(), existingWords))
        .put("lexical", ClosestWord.findLexicalClosestWord(wordToProcess.getValue(), existingWords));

      LOGGER.log(Level.INFO, "Time spent: {0} ms", Duration.between(startTime, Instant.now()).toMillis());
      rc.response().end(jsonObject.encode());
    });
  }

  public void save(RoutingContext rc) {
    JsonObject body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "request body: {0}", body);
    Word word = Word.of(body.mapTo(CreateWordCommand.class).getText());
    this.texts.wordExists(word.getValue()).onSuccess(exists -> {
      if (!exists)
        this.texts.save(word)
          .onSuccess(uuid -> LOGGER.log(Level.INFO, "Word saved: {0}",word.getValue()));
    });
  }

}
