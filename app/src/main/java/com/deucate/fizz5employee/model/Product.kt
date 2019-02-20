package com.deucate.fizz5employee.model

import java.io.Serializable

data class Product(
        val id: String,
        val name: String,
        val rating: Long,
        val hasDiscount: Boolean,
        val price: Long,
        val isStock: Boolean?,
        val discountedPrice: Long?,
        val description: String,
        val image: String,
        var quantity: Int = 0,
        var status:Boolean? = false
) : Serializable