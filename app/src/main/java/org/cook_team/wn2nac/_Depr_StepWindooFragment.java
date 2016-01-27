package org.cook_team.wn2nac;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class _Depr_StepWindooFragment extends android.support.v4.app.Fragment implements Switch.OnCheckedChangeListener, SensorEventListener {

    private static EventBus bus = EventBus.getDefault();
    private TextView status, wind, temperature, humidity, pressure;
    private Switch locationSwitch;
    private TextView latitude, longitude, altitude, locationTime, degreeTextView;
    private ImageView mPointer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout._depr_fragment_step_windoo, container, false);

        status = (TextView) rootView.findViewById(R.id.status);
        wind = (TextView) rootView.findViewById(R.id.wind);
        temperature = (TextView) rootView.findViewById(R.id.temperature);
        humidity = (TextView) rootView.findViewById(R.id.humidity);
        pressure = (TextView) rootView.findViewById(R.id.pressure);

        locationSwitch = (Switch) rootView.findViewById(R.id.switch1);
        latitude = (TextView) rootView.findViewById(R.id.latitude);
        longitude = (TextView) rootView.findViewById(R.id.longitude);
        //altitude = (TextView) rootView.findViewById(R.id.altitude);
        locationTime = (TextView) rootView.findViewById(R.id.locationTime);

        locationSwitch.setOnCheckedChangeListener(null);
        locationSwitch.setChecked(Wn2nacMap.locationEnabled);
        locationSwitch.setOnCheckedChangeListener(this);
        bus.post(new Wn2nacMap.LocationDisplayEvent(Wn2nacMap.lastLocation));

        degreeTextView = (TextView) rootView.findViewById(R.id.degreeTextView);
        mPointer = (ImageView) rootView.findViewById(R.id.pointer);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        updateDisplay();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        updateDisplay();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        super.onPause();
    }

    public void updateDisplay() {
        if (Wn2nacService.calibrated) status.setText("已校正");
        else if (Wn2nacService.available) status.setText("已連接 (需校正)");
        else status.setText("未連接");

        if (Wn2nacService.liveMeasurement.hasWindSpeed())
            wind.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getWind()));
        if (Wn2nacService.liveMeasurement.hasTemperature())
            temperature.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getTemperature()));
        if (Wn2nacService.liveMeasurement.hasHumidity())
            humidity.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getHumidity()));
        if (Wn2nacService.liveMeasurement.hasPressure())
            pressure.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getPressure()));
    }

    public void onEventMainThread(WindooEvent event) {
        if (event.getType() == JDCWindooEvent.JDCWindooAvailable)
            status.setText("已連接 (需校正)");
        else if (event.getType() == JDCWindooEvent.JDCWindooNotAvailable)
            status.setText("未連接");
        else if (event.getType() == JDCWindooEvent.JDCWindooCalibrated)
            status.setText("已校正");
        else if (event.getType() == JDCWindooEvent.JDCWindooVolumeNotAtItsMaximum)
            status.setText("請將音量調至最大");
        else if (event.getType() == JDCWindooEvent.JDCWindooPublishSuccess)
            status.setText("JDCWindooPublishSuccess : " + event.getData());
        else if (event.getType() == JDCWindooEvent.JDCWindooPublishException)
            status.setText("JDCWindooPublishException : " + event.getData());
        else if (event.getType() == JDCWindooEvent.JDCWindooNewWindValue)
            wind.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewTemperatureValue)
            temperature.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewHumidityValue)
            humidity.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewPressureValue)
            pressure.setText(String.format("%.2f", (double) event.getData()));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        locationSwitch.setOnCheckedChangeListener(null);
        locationSwitch.setChecked(Wn2nacMap.locationEnabled);
        locationSwitch.setOnCheckedChangeListener(this);
        if(isChecked) {
            if(Wn2nacMap.lastLocation == null) bus.post(new Wn2nacMap.LocationFetchEvent());
            bus.post(new Wn2nacMap.LocationEnableEvent());
        } else{
            bus.post(new Wn2nacMap.LocationDisableEvent());
        }
    }

    public void onEventMainThread(Wn2nacMap.LocationEnabledEvent event) {
    }

    public void onEventMainThread(Wn2nacMap.LocationDisplayEvent event) {
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
            degreeTextView.setText(String.format("%.1f", (double) azimuthInDegress));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}