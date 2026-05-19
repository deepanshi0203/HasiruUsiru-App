package com.hasiruusiru.app.data.model

data class Tree(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speciesName: String = "",
    val speciesNameKannada: String = "",
    val girth: Double = 0.0,        // in cm
    val health: String = "Good",    // Good / Fair / Poor
    val isEmptyPit: Boolean = false,
    val photoUri: String = "",
    val oxygenScore: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = ""
)
