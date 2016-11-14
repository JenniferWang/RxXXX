package com.wikipediaservice;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

public class WikipediaService {
  // TODO: add cache and keep the http connection.

  private static final String BASE_URL = "https://en.wikipedia.org/w/api.php";

  public static Observable<SearchResponse> search(final String term) {
    return Observable.fromCallable(new Callable<SearchResponse>() {
      @Override
      public SearchResponse call() throws Exception {
        HttpURLConnection connection = null;
        try {
          StringBuilder request = new StringBuilder(BASE_URL);
          request.append("?action=opensearch");
          request.append("&search=" + URLEncoder.encode(term, "utf8"));
          request.append("&format=json");

          URL url = new URL(request.toString());
          connection = (HttpURLConnection) url.openConnection();

          // Handle response
          JsonFactory jsonF = new JsonFactory();
          JsonParser jp = jsonF.createParser(connection.getInputStream());
          return new SearchResponse(term, jp);
        } finally {
          if (connection != null) {
            connection.disconnect();
          }
        }
      }
    }).subscribeOn(Schedulers.newThread()); // execute on background thread
  }
}
