package com.mercadopago.android.px.addons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.base.TintableImageView
import com.mercadopago.example.R

class MockFingerprintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint_validation)
        with(findViewById<TintableImageView>(R.id.fingerprint)) {
            setOnClickListener {
                val data = Intent()
                data.putExtra(BehaviourProvider.getSecurityBehaviour().extraResultKey, false)
                setResult(Activity.RESULT_OK, data)
                finish()
                setPendingTransition(this@MockFingerprintActivity)
            }
            setColorFilter(resources.getColorStateList(R.color.selector_fingerprint))
        }
    }

    companion object {
        fun start(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(getIntent(activity), requestCode)
            setPendingTransition(activity)
        }

        fun start(fragment: Fragment, requestCode: Int) {
            fragment.activity?.let {
                fragment.startActivityForResult(getIntent(it), requestCode)
                setPendingTransition(it)
            }
        }

        private fun getIntent(context: Context) = Intent(context, MockFingerprintActivity::class.java)
        private fun setPendingTransition(activity: Activity) {
            activity.overridePendingTransition(R.anim.px_slide_up_in, R.anim.px_slide_down_out)
        }
    }
}