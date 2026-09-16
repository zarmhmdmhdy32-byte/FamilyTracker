package com.familytracker.parent

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Circle

class MainActivity:ComponentActivity(){
    private val db=FirebaseFirestore.getInstance();private val auth=FirebaseAuth.getInstance()
    private lateinit var map:MapView;private var marker:Marker?=null;private var circle:Circle?=null
    override fun onCreate(b:Bundle?){super.onCreate(b);Configuration.getInstance().load(this,getSharedPreferences("osm",0))
        if(auth.currentUser==null)auth.signInAnonymously().addOnSuccessListener{login()}else login()}
    private fun login(){
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(40,40,40,40)}
        val input=EditText(this).apply{hint="توکن ۵ رقمی";inputType=2};val btn=Button(this).apply{text="اتصال"}
        box.addView(TextView(this).apply{text="موقعیت مکانی";textSize=28f});box.addView(input);box.addView(btn);setContentView(box)
        btn.setOnClickListener{
            val t=input.text.toString();if(t.length!=5){input.error="۵ رقم وارد کنید";return@setOnClickListener}
            db.collection("children").whereEqualTo("token",t).limit(1).get().addOnSuccessListener{q->
                if(q.isEmpty)input.error="توکن پیدا نشد" else mapScreen(q.documents[0].id)
            }
        }
    }
    private fun mapScreen(id:String){
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};val bar=LinearLayout(this)
        val home=Button(this).apply{text="ثبت خانه (مرکز نقشه)"};bar.addView(home);root.addView(bar)
        map=MapView(this);map.setMultiTouchControls(true);map.controller.setZoom(18.0);root.addView(map,LinearLayout.LayoutParams(-1,0,1f));setContentView(root)
        home.setOnClickListener{val p=map.mapCenter as GeoPoint
            db.collection("children").document(id).update("homeLat",p.latitude,"homeLng",p.longitude,"radius",10.0);drawCircle(p)}
        db.collection("children").document(id).addSnapshotListener{d,e->
            if(e!=null||d==null||!d.exists())return@addSnapshotListener
            val lat=d.getDouble("lat");val lng=d.getDouble("lng");if(lat==null||lng==null)return@addSnapshotListener
            val p=GeoPoint(lat,lng);if(marker==null){marker=Marker(map);map.overlays.add(marker)}
            marker!!.position=p;marker!!.title="فرزند";map.controller.animateTo(p)
            val hl=d.getDouble("homeLat");val hw=d.getDouble("homeLng");if(hl!=null&&hw!=null)drawCircle(GeoPoint(hl,hw))
            val r=d.getDouble("radius")?:10.0;if(hl!=null&&hw!=null){val a=FloatArray(1)
                android.location.Location.distanceBetween(hl,hw,lat,lng,a)
                if(a[0]>r)Toast.makeText(this,"هشدار: خارج از محدوده ۱۰ متری",Toast.LENGTH_LONG).show()}
            map.invalidate()
        }
    }
    private fun drawCircle(p:GeoPoint){circle?.let{map.overlays.remove(it)}
        circle=Circle().apply{center=p;radius=10.0};map.overlays.add(circle);map.invalidate()}
}
