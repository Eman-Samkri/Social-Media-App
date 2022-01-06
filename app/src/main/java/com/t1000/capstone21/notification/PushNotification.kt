package com.t1000.capstone21.notification

import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.t1000.capstone21.models.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String?
)