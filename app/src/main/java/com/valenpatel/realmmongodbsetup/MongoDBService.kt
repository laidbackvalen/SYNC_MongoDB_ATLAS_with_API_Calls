package com.valenpatel.realmmongodbsetup

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MongoDBService {

    @POST("action/find")
    fun fetchDocuments(
        @Header("api-key") apiKey: String,
        @Body requestBody: RequestBody
    ): Call<DocumentsResponse>

    @POST("action/updateOne")
    fun updateDocument(
        @Header("api-key") apiKey: String,
        @Body requestBody: RequestBody
    ): Call<UpdateResponse>
}
