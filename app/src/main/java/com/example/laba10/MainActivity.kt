package com.example.laba10

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var myService: TimeService? = null
    var isBound = false
    var receiver: BroadcastReceiver? = null
    val BROADCAST_TIME_EVENT = "com.example.laba10.timeevent"
    private var updateInterval = 1
    private var currentInitialCount = 0

    val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimeService.MyBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intervalEditText = findViewById<EditText>(R.id.interval);
        var initialEditText = findViewById<EditText>(R.id.initial);
        initialEditText.setText(currentInitialCount.toString())
        intervalEditText.setText(updateInterval.toString())
        receiver = object : BroadcastReceiver() {
            // Получено широковещательное сообщение
            override fun onReceive(context: Context?, intent: Intent) {
                val counter = intent.getIntExtra("counter", 0)
                val textCounter = findViewById<TextView>(R.id.textCounter)
                textCounter.text = counter.toString()
            }
        }
        val filter = IntentFilter(BROADCAST_TIME_EVENT)
        // Регистрация ресивера и фильтра
        registerReceiver(receiver, filter)
    }

    fun buttonStartService(view: View) {
        val intent = Intent(this, TimeService::class.java)
        var intervalEditText: Int = findViewById<EditText>(R.id.interval).text.toString().toInt();
        var initialEditText: Int = findViewById<EditText>(R.id.initial).text.toString().toInt();
        intent.putExtra("updateInterval", intervalEditText)
        intent.putExtra("currentInitialCount", initialEditText)
        startService(intent)
        bindService(
            Intent(this, TimeService::class.java),
            myConnection, Context.BIND_AUTO_CREATE
        )

    }

    fun buttonStopService(view: View) {
        stopService(Intent(this, TimeService::class.java))
        unbindService(myConnection)
    }

    fun buttonGetValue(view: View) {
        if (isBound)
            findViewById<TextView>(R.id.textCounter).text =
                myService!!.getCounter().toString()
    }

    fun changeInterval(view: View) {
        this.updateInterval = findViewById<EditText>(R.id.interval).text.toString().toInt();
        if (isBound)
            myService!!.setUpdateInterval(updateInterval)

    }

    override fun onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy()
    }

}