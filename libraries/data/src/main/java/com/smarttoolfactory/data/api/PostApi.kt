package com.smarttoolfactory.data.api

import com.smarttoolfactory.data.model.PostDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface PostApi {

    @GET("/posts")
    suspend fun getPosts(): List<PostDTO>
}

interface PostApiRxJava {
    @GET("/posts")
    fun getPostsSingle(): Single<List<PostDTO>>
}

const val BASE_URL = "https://jsonplaceholder.typicode.com"
