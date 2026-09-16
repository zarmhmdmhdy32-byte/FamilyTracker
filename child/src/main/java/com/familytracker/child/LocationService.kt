package com.familytracker.child

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationService:Service(){
    private lateinit var client:FusedLocationProviderClient
    private val db=FirebaseFirestore.getInstance()
    override fun onCreate(){
        super.onCreate()
        val ch=NotificationChannel("location","موقعیت مکانی",NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        startForeground(10,NotificationCompat.Builder(this,"location")
            .setContentTitle("مار بازی").setContentText("اشتراک‌گذاری موقعیت فعال است")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation).build())
        client=LocationServices.getFusedLocationProviderClient(this)
        val req=LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,5000)
            .setMinUpdateIntervalMillis(3000).build()
        client.requestLocationUpdates(req,callback,mainLooper)
    }
    private val callback=object:LocationCallback(){
        override fun onLocationResult(r:LocationResult){
            val l:Location=r.lastLocation?:return
            val id=FirebaseAuth.getInstance().currentUser?.uid?:return
            db.collection("children").document(id).update(
                mapOf("lat" to l.latitude,"lng" to l.longitude,"accuracy" to l.accuracy,
                "gpsEnabled" to true,"online" to true,"updatedAt" to System.currentTimeMillis()))
        }
    }
    override fun onDestroy(){if(::client.isInitialized)client.removeLocationUpdates(callback);super.onDestroy()}
    override fun onBind(i:Intent?):IBinder?=null
}
