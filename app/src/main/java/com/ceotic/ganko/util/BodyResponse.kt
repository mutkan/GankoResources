package com.ceotic.ganko.util

class BodyResponse<T>(val success: Boolean,
                      val data: T,
                      val error: String)