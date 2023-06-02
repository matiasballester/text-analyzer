package com.cloudinx.textanalyzer.model;

import java.util.UUID;

public class Word {
  UUID id;
  String value;

  public static Word of(String value) {
    return of(null, value);
  }

  public static Word of(UUID id, String value) {
    Word data = new Word();
    data.setId(id);
    data.setValue(value);
    return data;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Word{" +
      "id='" + id + '\'' +
      ", value='" + value +
      '}';
  }
}
