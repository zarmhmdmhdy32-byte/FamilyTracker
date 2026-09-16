package com.familytracker.child

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val prefs by lazy { getSharedPreferences("child", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser == null) auth.signInAnonymously().addOnSuccessListener { startFlow() }
        else startFlow()
    }

    private fun startFlow() {
        val token = prefs.getString("token", null)
        if (token == null) showTokenScreen() else showGameScreen(token)
    }

    private fun showTokenScreen() {
        val box = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(40,60,40,40) }
        val title = TextView(this).apply { text="توکن اتصال"; textSize=26f }
        val info = TextView(this).apply { text="این کد را به والد بدهید."; textSize=16f }
        val tokenView = TextView(this).apply { textSize=34f }
        val button = Button(this).apply { text="ساخت توکن ۵ رقمی" }
        box.addView(title); box.addView(info); box.addView(tokenView); box.addView(button)
        setContentView(box)
        button.setOnClickListener {
            val token=Random.nextInt(10000,100000).toString()
            db.collection("children").document(auth.currentUser!!.uid).set(
                mapOf("token" to token, "online" to true, "gpsEnabled" to true,
                    "updatedAt" to System.currentTimeMillis())
            ).addOnSuccessListener {
                prefs.edit().putString("token",token).apply()
                showGameScreen(token)
            }
        }
    }

    private fun showGameScreen(token:String) {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(24,24,24,24)}
        root.addView(TextView(this).apply{
            text="مار بازی\nکد اتصال: $token\nاشتراک‌گذاری موقعیت: فعال"; textSize=20f
        })
        root.addView(SnakeView(this), LinearLayout.LayoutParams(-1,0,1f))
        root.addView(TextView(this).apply{text="موقعیت با مجوز Android به اشتراک گذاشته می‌شود."})
        setContentView(root)
        requestLocation()
    }

    private fun requestLocation() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),100)
        else startLocationService()
    }
    override fun onRequestPermissionsResult(r:Int,p:Array<out String>,g:IntArray){
        super.onRequestPermissionsResult(r,p,g)
        if(r==100 && g.any{it==PackageManager.PERMISSION_GRANTED}) startLocationService()
    }
    private fun startLocationService() {
        ContextCompat.startForegroundService(this,Intent(this,LocationService::class.java))
    }
}
