package com.cloudinx.textanalyzer.exceptions;

import java.util.UUID;

public class WordNotFoundException extends RuntimeException {
  public WordNotFoundException(UUID id) {
    super("Word id: " + id + " was not found. ");
  }

  public WordNotFoundException(String value) {
    super("Word: " + value + " was not found. ");
  }
}
