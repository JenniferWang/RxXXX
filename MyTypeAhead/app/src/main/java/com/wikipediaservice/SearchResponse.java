package com.wikipediaservice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

public class SearchResponse {
  private ArrayList<Entry> mEntries = new ArrayList<>();

  public ArrayList<Entry> getEntries() {
    return mEntries;
  }

  public SearchResponse(String searchedTerm, JsonParser jp) throws IOException {
    if (jp.nextToken() != JsonToken.START_ARRAY) {
      throw new IOException("Expected response to be a JSON array");
    }
    if (!jp.nextTextValue().equals(searchedTerm)) {
      throw new IOException("Expected response corresponds to " + searchedTerm + " but got " + jp.currentToken());
    }
    ArrayList<String> titles = parseStringArray(jp);
    ArrayList<String> descriptions = parseStringArray(jp);
    ArrayList<String> links = parseStringArray(jp);
    if (!(titles.size() == links.size() && titles.size() == descriptions.size())) {
      throw new IOException("Expected number of titles, descriptions and links are equal");
    }
    for (int i = 0; i < titles.size(); i++) {
      this.mEntries.add(new Entry(titles.get(i), descriptions.get(i), links.get(i)));
    }
  }

  public class Entry {
    final public String title;
    final public String description;
    final public String link;

    public Entry(String title, String description, String link) {
      this.title = title;
      this.description = description;
      this.link = link;
    }
  }

  private static ArrayList<String> parseStringArray(JsonParser jp) throws IOException {
    if (jp.nextToken() != JsonToken.START_ARRAY) {
      throw new IOException("Expected token to be start arrary, but get " + jp.currentToken());
    }
    ArrayList<String> xs = new ArrayList();
    while (jp.nextToken() != JsonToken.END_ARRAY) {
      xs.add(jp.getText());
    }
    return xs;
  }
}
