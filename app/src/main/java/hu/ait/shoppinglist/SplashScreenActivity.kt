package hu.ait.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.lang.Exception

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_transition)
        anim.duration = 2000
        splashScreen.startAnimation(anim)


        Handler().postDelayed({
            var intent = Intent()
            intent.setClass(this@SplashScreenActivity, ScrollingActivity::class.java)
            startActivity(intent)
            finish()
        },2000)




    }
}
