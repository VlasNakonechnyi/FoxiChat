package com.example.foxichat.service

import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.foxichat.MainActivity
import com.example.foxichat.R
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class MessagingService : FirebaseMessagingService() {


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val displayName = remoteMessage.data["author_name"]
            val body = remoteMessage.data["body"]
            val timestamp = remoteMessage.data["timestamp"]

            val notification = NotificationCompat.Builder(this, MainActivity.FCM_CHANNEL_ID)
                .setSmallIcon(R.drawable.logotype)
                .setContentTitle(displayName)
                .setContentText(body)
                .build()

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1002, notification)
        }

        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//            it.body?.let { body -> sendNotification(body) }
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private fun isLongRunningJob() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {

        val retrofit = RetrofitClient.getClient()
        val apiService = retrofit.create(ApiFactory::class.java)
        if (token != null) {
            val body = mapOf(
                "id" to token,
//                "userId" to .auth.currentUser?.uid!!,
//                "deviceId" to ChatAuth.auth.currentUser?.uid!!,
                "timestamp" to LocalDateTime.now().toString()
            )
            apiService.postRequest(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d(TAG, response.body().toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                }
            })
        } else {
            //apiService.postRequest("invalid")
        }
        //Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
//    private fun sendNotification(messageBody: String) {
//        val requestCode = 0
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            requestCode,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE,
//        )
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setContentTitle(getString(R.string.fcm_message))
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT,
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationId = 0
//        notificationManager.notify(notificationId, notificationBuilder.build())
//    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }

}