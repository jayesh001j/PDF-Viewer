package com.example.pdfviewer.model

data class PdfModel(
    val name: String,
    val filePath: String,
    var isFavorite: Boolean = false
)
