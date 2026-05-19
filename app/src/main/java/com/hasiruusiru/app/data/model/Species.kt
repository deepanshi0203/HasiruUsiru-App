package com.hasiruusiru.app.data.model

data class Species(
    val id: String,
    val nameEnglish: String,
    val nameKannada: String,
    val scientificName: String,
    val description: String,
    val descriptionKannada: String,
    val oxygenFactor: Double,       // used to compute oxygen score
    val imageRes: Int = 0,
    val isNative: Boolean = true
)
