package com.example.bevasarlolista.model

import java.io.Serializable
import java.util.Date

data class Item(
    val id: Int,
    val name: String,
    val amount: Double,
    val price: Double,
    val purchaseDate: Date,
    val forUser: User?,
    var checkedBy: User?
) : Serializable