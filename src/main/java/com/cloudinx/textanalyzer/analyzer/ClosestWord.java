package com.cloudinx.textanalyzer.analyzer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import static java.lang.Math.*;

public class ClosestWord {

  public static String findLexicalClosestWord(String wordToAnalyze, List<String> words) {
    String closestWord = null;
    List<String> closestWords = new ArrayList<>(words.size());
    int minDifference = Integer.MAX_VALUE;

    for (String word : words) {
      int difference = word.compareTo(wordToAnalyze);
      if (difference < minDifference) {
        minDifference = difference;
        closestWords.clear();
        closestWords.add(word);
      } else if (difference == minDifference) {
        closestWords.add(word);
      }
    }
    if (!closestWords.isEmpty()) {
      Collections.sort(closestWords, Comparator.naturalOrder());
      closestWord = closestWords.get(0);
    }
    return closestWord;
  }

  public static String find(String wordToAnalyze, List<String> words) {
    int requestValue = calculateWordValue(wordToAnalyze);
    int closestValue = Integer.MAX_VALUE;
    int largestValue = Integer.MIN_VALUE;
    String closestWord = null;

    for (String word : words) {
      int wordValue = calculateWordValue(word);

      // Check if the word is closer to the request
      if (abs(wordValue - requestValue) < abs(closestValue - requestValue)) {
        closestValue = wordValue;
        largestValue = wordValue;
        closestWord = word;
      }
      // Check if the word has the same closest value but a larger value
      else {
        boolean areWordsEquals = abs(wordValue - requestValue) == abs(closestValue - requestValue);
        if (areWordsEquals && wordValue > largestValue) {
          largestValue = wordValue;
          closestWord = word;
        }
        // Check if the word has the same closest value and the same largest value
        else if (areWordsEquals && wordValue == largestValue && word.compareTo(closestWord) > 0) {
          closestWord = word;
        }
      }
    }

    return closestWord;
  }

  private static int calculateWordValue(String word) {
    int value = 0;
    for (char c : word.toLowerCase().toCharArray())
      value += c - 'a' + 1;
    return value;
  }
}
