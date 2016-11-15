package com.modules.wikipediaservice;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WikipediaService {
  private static final String BASE_URL = "https://en.wikipedia.org/w/api.php";
  private Map<String, SearchResponse> mCache = new HashMap<>();

  private Object mLock = new Object();

  public WikipediaService(){}

  public Observable<SearchResponse> search(final String term) {
    if (term.length() == 0) {
      return Observable.just(new SearchResponse());
    }
    if (mCache.containsKey(term)) {
      return Observable.just(mCache.get(term));
    }

    return Observable.fromCallable(() -> {
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

        synchronized (mLock) {
          mCache.put(term, new SearchResponse(term, jp));
        }
        return mCache.get(term);
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }).subscribeOn(Schedulers.newThread()); // execute on background thread
  }
}
