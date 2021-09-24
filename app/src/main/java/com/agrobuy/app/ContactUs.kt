package com.agrobuy.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContactUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_support)
        // calling link
        val callSpan = SpannableString(getString(R.string.contact_us))
        val v : TextView = findViewById(R.id.textView15)
        callSpan.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + getString(R.string.phone_number))
                startActivity(callIntent)
            }
        }, 43, 56, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        v.text = callSpan
        v.movementMethod = LinkMovementMethod.getInstance()
    }
}