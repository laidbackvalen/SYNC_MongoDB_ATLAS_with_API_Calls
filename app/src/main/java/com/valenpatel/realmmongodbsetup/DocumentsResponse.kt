package com.valenpatel.realmmongodbsetup

data class Document(
    val _id: String,
    val name: String,
    val age: Double,
    val city: String
)

data class DocumentsResponse(
    val documents: List<Document>
)