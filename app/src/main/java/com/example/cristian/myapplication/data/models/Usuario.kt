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
class UserLogin(var username: String,var pass: String)
class UserResponse(var id: String,var username: String,var estado: String)
class LoginResponse(var user:String,var token:String)
class ResetPasswordResponse(var email:String,var estado:String)
class ResetUserEmail(var email:String)