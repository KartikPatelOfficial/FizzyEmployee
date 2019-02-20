package com.deucate.fizz5employee.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Order(
    val id: String = "",
    val paymentMethod: Int,
    val address: String,
    var products: ArrayList<Product>,
    val martName: String,
    val martID: String,
    val status: Int,
    val time: Timestamp,
    val userName: String,
    val userID: String,
    val totalAmount: Long
) : Serializable