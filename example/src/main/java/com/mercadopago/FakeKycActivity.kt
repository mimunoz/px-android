package com.mercadopago

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mercadopago.example.R


class FakeKycActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_kyc)

        val callback = intent.data?.getQueryParameter(QUERY_CALLBACK)

        findViewById<TextView>(R.id.initiative).text = intent.data?.getQueryParameter(QUERY_INITIATIVE) ?: "NO INITIATIVE"
        findViewById<TextView>(R.id.callback).text = callback ?: "NO CALLBACK"

        findViewById<View>(R.id.yes_button).setOnClickListener {
            if (callback.isNullOrEmpty()) {
                Toast.makeText(this, "No callback to call", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(callback) }
            val isIntentSafe = packageManager.queryIntentActivities(intent, 0).size > 0
            if (isIntentSafe) {
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "No one listening to callback", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.no_button).setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val QUERY_INITIATIVE = "initiative"
        private const val QUERY_CALLBACK = "callback"
    }
}