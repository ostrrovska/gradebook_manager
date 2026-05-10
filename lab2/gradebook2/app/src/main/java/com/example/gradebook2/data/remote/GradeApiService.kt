package com.example.gradebook2.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Lab 9, Task 1 — Retrofit service with the 4 required CRUD endpoints
interface GradeApiService {

    // GET /grades — fetch full list for caching
    @GET("grades")
    suspend fun getGrades(): Response<List<GradeDto>>

    // GET /grades/{id} — fetch single record for detail screen (Lab 9, Task 3)
    @GET("grades/{id}")
    suspend fun getGradeById(@Path("id") id: String): Response<GradeDto>

    // POST /grades — create new record (Lab 9, Task 4)
    @POST("grades")
    suspend fun createGrade(@Body dto: GradeCreateDto): Response<GradeDto>

    // DELETE /grades/{id} — remove record (Lab 9, Task 4)
    @DELETE("grades/{id}")
    suspend fun deleteGrade(@Path("id") id: String): Response<Unit>
}
