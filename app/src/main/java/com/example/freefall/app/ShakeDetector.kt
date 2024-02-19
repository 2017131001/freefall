package com.example.freefall.app

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

class ShakeDetector : SensorEventListener {
    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0
    private var lastMovementFall: Long = 0
    private var movementStart: Long = 0

    fun setOnShakeListener(listener: OnShakeListener?) {
        mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) { // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {
//        if (event.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
        if (mListener != null) {
            movementStart = System.currentTimeMillis()

            val loX = event.values[0]
            val loY = event.values[1]
            val loZ = event.values[2]

            val loAccelerationReader = sqrt(
                loX.toDouble().pow(2.0)
                        + loY.toDouble().pow(2.0)
                        + loZ.toDouble().pow(2.0)
            )

            val precision = DecimalFormat("0.00")
            val ldAccRound = java.lang.Double.parseDouble(precision.format(loAccelerationReader))

            // precision/fall detection and more than 1000ms after last fall
            if (ldAccRound > 0.3 && ldAccRound < 1.3 && (movementStart - lastMovementFall) > 1000) {

                mShakeCount++
                mListener!!.onShake(mShakeCount)

                lastMovementFall = System.currentTimeMillis()

            }
        }
//        if (mListener != null) {
//            val x = event.values[0]
//            val y = event.values[1]
//            val z = event.values[2]
//            val gX = x / SensorManager.GRAVITY_EARTH
//            val gY = y / SensorManager.GRAVITY_EARTH
//            val gZ = z / SensorManager.GRAVITY_EARTH
//            // gForce will be close to 1 when there is no movement.
//            val gForce: Float = sqrt(gX * gX + gY * gY + gZ * gZ)
//            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
//                val now = System.currentTimeMillis()
//                // ignore shake events too close to each other (500ms)
//                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
//                    return
//                }
//                // reset the shake count after 3 seconds of no shakes
//                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
//                    mShakeCount = 0
//                }
//                mShakeTimestamp = now
//                mShakeCount++
//                mListener!!.onShake(mShakeCount)
//            }
//        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
}