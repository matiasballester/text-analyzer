package com.cloudinx.textanalyzer;

import com.cloudinx.textanalyzer.handlers.TextAnalyzerHandler;
import com.cloudinx.textanalyzer.repository.WordRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
  private final static Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.log(Level.INFO, "Starting HTTP server...");

    PgPool pgPool = pgPool();
    WordRepository textRepository = WordRepository.create(pgPool);
    TextAnalyzerHandler textAnalyzerHandler = TextAnalyzerHandler.create(textRepository);

    // Configure routes
    Router router = routes(textAnalyzerHandler);

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(8888)
      // Print the port
      .onSuccess(server -> {
        startPromise.complete();
        System.out.println("HTTP server started on port " + server.actualPort());
      })
      .onFailure(event -> {
        startPromise.fail(event);
        System.out.println("Failed to start HTTP server:" + event.getMessage());
      });

    vertx.executeBlocking(future -> {
      DataInitializer dataInitializer = new DataInitializer(pgPool);
      dataInitializer.run();
      future.complete();
    }, false, asyncResult -> LOGGER.log(Level.INFO, "Done!"));
  }

  private Router routes(TextAnalyzerHandler handlers) {
    Router router = Router.router(vertx);

    router.post("/analyze").consumes("application/json")
      .handler(BodyHandler.create()).handler(handlers::analyze);
    router.get("/words").produces("application/json")
      .handler(handlers::all);
    router.get("/health").handler(rc -> rc.response().end("Text analyzer is up and running"));

    return router;
  }

  private PgPool pgPool() {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("analyzer")
      .setUser("user")
      .setPassword("password");

    // Pool Options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    // Create the pool from the data object
    PgPool pool = PgPool.pool(vertx, connectOptions, poolOptions);

    return pool;
  }
}
