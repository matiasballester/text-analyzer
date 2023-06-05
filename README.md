# text-analyzer
Text Analyzer Tool

This application was generated using http://start.vertx.io

== Building

To launch your tests:
```
./mvnw clean test
```

To package your application:
```
./mvnw clean package
```

To run your application:
```
./mvnw clean compile exec:java
```

Please first run docker compose up to start postgres docker image. It's required by the tool.
By doing it when the app is started it will initialize the database with some initial data to test the text-analyzer tool.
Because of it item 5 will never be reached:

If no words are found to match against (as in the first request), the server will return null
for both response fields.

To make it work you need to remove database initialization.

```
docker compose up
```