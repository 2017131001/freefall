package com.example.freefall.app

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.freefall.R
import kotlinx.android.synthetic.main.activity_sub.*

class SubActivity : AppCompatActivity() {

    // The following are used for the shake detection
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null
    lateinit var sendMessageButton: Button
    lateinit var resetButton: Button
    private lateinit var title: TextView
    private var input = "01099248069"

    companion object {
        var check = false
        var check2 = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        permissionCheck()
        resetButton = findViewById(R.id.resetButton)
        initSensor()

        resetButton.visibility = View.GONE
        title1.visibility = View.GONE
        phoneNumber.visibility = View.GONE
        okButton.visibility = View.GONE


        startButton.setOnClickListener {
            it.visibility = View.GONE
            fallTitle.visibility = View.GONE
            resetButton.visibility = View.VISIBLE
            title1.visibility = View.VISIBLE
            phoneNumber.visibility = View.VISIBLE
            okButton.visibility = View.VISIBLE
        }

        okButton.setOnClickListener {
            check2 = true
            phoneNumber.visibility = View.GONE
            okButton.visibility = View.GONE
            input = phoneNumber.text.toString()
            inputPhoneNumber.text = input
            title1.text = "입력된 번호"
        }

        resetButton.setOnClickListener {
            check = false
        }
    }

    override fun onResume() {
        super.onResume()
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager!!.registerListener(
            mShakeDetector,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() { // Add the following line to unregister the Sensor Manager onPause
        mSensorManager!!.unregisterListener(mShakeDetector)
        super.onPause()
    }

    private fun initSensor() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) { /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                if (check2) {
                    if (!check) {
                        Toast.makeText(this@SubActivity, "낙상이 감지되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                        sendSMS()
                    } else {
                        Toast.makeText(this@SubActivity, "초기화 버튼 클릭해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                Log.d("++onShake", "시작버튼 클릭 전")
            }
        })
    }

    private fun permissionCheck() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS
                )
            ) {
                Toast.makeText(this@SubActivity, "SMS 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
            val permission = arrayOf(
                Manifest.permission.SEND_SMS
            )
            ActivityCompat.requestPermissions(
                this,
                permission,
                1
            )
        }
    }

    fun sendSMS() {
//        val pi: PendingIntent =
//            PendingIntent.getActivity(this, 0, null, 0)
        val sms: SmsManager = SmsManager.getDefault()

        try {
            sms.sendTextMessage(input, null, "낙상이 감지되었습니다.", null, null)
            Toast.makeText(this, "성공적으로 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            check = true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "메시지 전송을 실패하였습니다.", Toast.LENGTH_SHORT).show()
            check = false
        }
    }
}