package com.cloudinx.textanalyzer.command;

import java.io.Serializable;
import java.util.Objects;

public class CreateWordCommand implements Serializable {

  private String text;

  public static CreateWordCommand of(String text) {
    CreateWordCommand textCommand = new CreateWordCommand();
    textCommand.setText(text);
    return textCommand;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateWordCommand postForm = (CreateWordCommand) o;
    return text.equals(postForm.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public String toString() {
    return "Word{" +
      "value='" + text +
      '}';
  }
}
