package com.example.jiyue.mytypeahead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import com.wikipediaservice.SearchResponse;
import com.wikipediaservice.WikipediaService;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
  private Subject<String, String> mInputStream;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    addSearchBarListener();
  }

  protected void addSearchBarListener() {
    mInputStream = PublishSubject.create();
    mInputStream
        .subscribeOn(Schedulers.newThread())
        .debounce(400, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
          @Override
          public void call(String s) {
            requestWikipediaService(s);
          }
        });

    EditText editText = (EditText) findViewById(R.id.search);
    TextWatcher tw = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // call the `onNext` method on all the subscriber to `mInputStream`
        mInputStream.onNext(charSequence.toString());
      }

      @Override
      public void afterTextChanged(Editable editable) {}
    };
    editText.addTextChangedListener(tw);
  }

  protected void onResponseReceived(SearchResponse response) {
    StringBuilder outputBuilder = new StringBuilder();
    for (SearchResponse.Entry entry: response.getEntries()) {
      outputBuilder.append(entry.title);
      outputBuilder.append("\n");
    }
    TextView textView = (TextView) findViewById(R.id.response);
    textView.setText(outputBuilder.toString());
  }

  protected void onErrorReceived(Throwable t) {
    TextView textView = (TextView) findViewById(R.id.response);
    textView.setText(t.getMessage());
  }

  protected void requestWikipediaService(String term) {
    WikipediaService.search(term)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<SearchResponse>() {
          @Override
          public void call(SearchResponse searchResponse) {
            onResponseReceived(searchResponse);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            onErrorReceived(throwable);
          }
        });
  }
}
