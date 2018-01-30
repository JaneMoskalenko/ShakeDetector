package com.example.jane.myapplication;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

  private Observable<?> shakeObservable;
  private Subscription shakeSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    shakeObservable = ShakeDetector.create(this);
  }

  private void vibrate(){
    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    long[] pattern = {0, 500, 1000};
    vibrator.vibrate(pattern, -1);

  }

  @Override
  protected void onResume() {
    super.onResume();
    shakeSubscription =  shakeObservable.subscribe(object -> {
      vibrate();
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    shakeSubscription.unsubscribe();
  }
}
