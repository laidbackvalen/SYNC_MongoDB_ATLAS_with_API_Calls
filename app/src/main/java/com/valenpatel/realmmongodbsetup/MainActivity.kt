package com.valenpatel.realmmongodbsetup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            Log.d("Interceptor", "Request URL: ${newRequest.url}")
            Log.d("Interceptor", "Request Headers: ${newRequest.headers}")
            return chain.proceed(newRequest)
        }
    }

    private lateinit var mongoDBService: MongoDBService
    private val apiKey = "YOUR_API_KEY" // Replace with your actual API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit with interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ap-south-1.aws.data.mongodb-api.com/app/data-kkkkamp/endpoint/data/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mongoDBService = retrofit.create(MongoDBService::class.java)

        // Fetch documents
        fetchDocuments()

        // Update document
        updateDocument()
    }

    private fun fetchDocuments() {
        val jsonQuery = """
            {
                "collection": "first",
                "database": "RealmOne",
                "dataSource": "RealmOne",
                "filter": {}
            }
        """.trimIndent()

        val requestBody = jsonQuery.toRequestBody("application/json".toMediaTypeOrNull())

        mongoDBService.fetchDocuments(apiKey, requestBody).enqueue(object : retrofit2.Callback<DocumentsResponse> {
            override fun onResponse(call: Call<DocumentsResponse>, response: retrofit2.Response<DocumentsResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("MongoDB Response", result.toString())
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MongoDB Error", errorBody ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<DocumentsResponse>, t: Throwable) {
                Log.e("MongoDB Failure", t.message ?: "Unknown error")
            }
        })
    }

    private fun updateDocument() {
        val jsonUpdate = """
            {
                "collection": "first",
                "database": "RealmOne",
                "dataSource": "RealmOne",
                "filter": {"name": "John Doe"},
                "update": {"\${'$'}set": {"name": "new name"}}
            }
        """.trimIndent()

        val requestBody = jsonUpdate.toRequestBody("application/json".toMediaTypeOrNull())

        mongoDBService.updateDocument(apiKey, requestBody).enqueue(object : retrofit2.Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: retrofit2.Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("MongoDB Response", result.toString())
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MongoDB Error", errorBody ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Log.e("MongoDB Failure", t.message ?: "Unknown error")
            }
        })
    }
}
