package com.familytracker.child
import android.content.Context
import android.graphics.*
import android.view.*
import kotlin.random.Random

class SnakeView(c:Context):View(c){
    private val p=Paint(1); private val snake=mutableListOf(Pair(8,8),Pair(7,8),Pair(6,8))
    private var food=Pair(14,10); private var dx=1; private var dy=0; private var last=0L; private var score=0
    private val n=20; private var sx=0f; private var sy=0f
    override fun onDraw(c:Canvas){
        val s=width.coerceAtMost(height)/n.toFloat(); p.color=Color.GREEN
        snake.forEach{c.drawRect(it.first*s,it.second*s,(it.first+1)*s,(it.second+1)*s,p)}
        p.color=Color.RED;c.drawRect(food.first*s,food.second*s,(food.first+1)*s,(food.second+1)*s,p)
        p.color=Color.WHITE;p.textSize=30f;c.drawText("امتیاز: $score",10f,height-15f,p)
        if(System.currentTimeMillis()-last>150){step();last=System.currentTimeMillis()};postInvalidateDelayed(30)
    }
    private fun step(){
        val h=snake.first();val q=Pair((h.first+dx+n)%n,(h.second+dy+n)%n)
        if(q in snake){snake.clear();snake.add(Pair(8,8));snake.add(Pair(7,8));snake.add(Pair(6,8));dx=1;dy=0;score=0;return}
        snake.add(0,q);if(q==food){score++;food=Pair(Random.nextInt(n),Random.nextInt(n))}else snake.removeAt(snake.lastIndex)
    }
    override fun onTouchEvent(e:MotionEvent):Boolean{
        if(e.action==MotionEvent.ACTION_DOWN){sx=e.x;sy=e.y}
        if(e.action==MotionEvent.ACTION_UP){
            val ax=kotlin.math.abs(e.x-sx);val ay=kotlin.math.abs(e.y-sy)
            if(ax>ay){if(e.x>sx&&dx==0){dx=1;dy=0};if(e.x<sx&&dx==0){dx=-1;dy=0}}
            else{if(e.y>sy&&dy==0){dy=1;dx=0};if(e.y<sy&&dy==0){dy=-1;dx=0}}
        };return true
    }
}
