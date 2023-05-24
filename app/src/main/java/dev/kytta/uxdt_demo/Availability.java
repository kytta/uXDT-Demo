package dev.kytta.uxdt_demo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;

public class Availability {

    private final SensorManager sensorManager;

    public Availability(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Checks if the device has a microphone.
     *
     * @return true if the microphone is available, false otherwise
     */
    public boolean isMicrophoneAvailable() {
        return AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) != AudioRecord.ERROR_BAD_VALUE;
    }

    /**
     * Checks if the device has an accelerometer.
     *
     * @return true if the accelerometer is available, false otherwise
     */
    public boolean isAccelerometerAvailable() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    /**
     * Checks if the device has a gyroscope.
     *
     * @return true if the gyroscope is available, false otherwise
     */
    public boolean isGyroscopeAvailable() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }
}
