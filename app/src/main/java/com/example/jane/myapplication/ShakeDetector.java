package com.example.jane.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jane on 30.01.18.
 */

public class ShakeDetector {

    public static final int THRESHOLD = 13;
    public static final int SHAKE_COUNT = 2;
    public static final int SHAKE_PERIOD = 1;

    @NonNull
    public static rx.Observable<?> create(@NonNull Context context){
      return createAcceleratorObservable(context)
          .map(sensorEvent -> new XEvent(sensorEvent.timestamp, sensorEvent.values[0]))
          .filter(xEvent -> Math.abs(xEvent.x)>THRESHOLD)
          .buffer(2,1)
          .filter(buf -> buf.get(0).x * buf.get(1).x <0)
          .map(buf -> buf.get(1).timestamp / 1000000000f)
          .buffer(SHAKE_COUNT, 1)
          .filter(buf ->buf.get(SHAKE_COUNT-1) - buf.get(0)< SHAKE_PERIOD)
          .throttleFirst(SHAKE_PERIOD, TimeUnit.SECONDS);


    }

    public static rx.Observable<SensorEvent> createAcceleratorObservable(@NonNull Context context){
      SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
      if (sensorList==null || sensorList.isEmpty()){
        throw new IllegalStateException("No accelerometer!");
      }

      return SensorEventObservableFactory.createSensorEventObservable(sensorList.get(0), sensorManager);
    }

    private static class XEvent{
      public final long timestamp;
      public final float x;

      private XEvent(long timestamp, float x) {
        this.timestamp = timestamp;
        this.x = x;
      }
    }

}
