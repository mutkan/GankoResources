package com.example.cristian.myapplication.data.models

class Usuario(
        var tipo: String,
        var nombre: String,
        var apellido: String,
        var email: String,
        var password: String,
        var dni: String,
        var estado: String,
        var rol: String,
        var plan: String
)

class UserResponse(var id: Int,var username: String,var estado: String)
class LoginResponse(var user:UserResponse,var token:String)
class ResetPasswordResponse(var email:String,var estado:String)
class ResetUserEmail(var email:String)