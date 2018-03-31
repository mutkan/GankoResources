package com.example.cristian.myapplication.util

class BodyResponse<T>(val success: Boolean,
                      val data: T,
                      val error: String)