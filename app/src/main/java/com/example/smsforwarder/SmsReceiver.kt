package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.ktx.Firebase
import com.google.firebase.functions.ktx.functions

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            try {
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>
                    for (pdu in pdus) {
                        val format = bundle.getString("format")
                        val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
                        val messageBody = sms.messageBody
                        val sender = sms.displayOriginatingAddress

                        // ✅ Log locally
                        Log.d("SmsReceiver", "📩 SMS from $sender: $messageBody")

                        // ✅ Forward to Firebase
                        sendToFirebase(sender, messageBody)
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Exception: ${e.message}")
            }
        }
    }

    private fun sendToFirebase(sender: String, message: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("SmsReceiver", "❌ User not signed in, cannot call Firebase function")
            return
        }

        val functions: FirebaseFunctions = Firebase.functions

        val data = hashMapOf(
            "uid" to user.uid,
            "sender" to sender,
            "message" to message
        )

        functions
            .getHttpsCallable("addExpenseFromSms")
            .call(data)
            .addOnSuccessListener { result: HttpsCallableResult ->
                Log.d("SmsReceiver", "✅ Firebase function success: ${result.data}")
            }
            .addOnFailureListener { e ->
                Log.e("SmsReceiver", "❌ Firebase function failed: ${e.message}")
            }
    }
}


