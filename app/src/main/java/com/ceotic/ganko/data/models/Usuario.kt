package com.ceotic.ganko.data.models

import java.util.*

class Usuario(
        var tipo: String,
        var nombre: String,
        var apellido: String,
        var email: String,
        var password: String,
        var dni: String,
        var estado: String,
        var rol: String,
        var plan: String,
        var ultimoPago: Date?
)
class UserLogin(var username: String,var pass: String)
class UserResponse(var id: String,var username: String,var estado: String)
class LoginData(var id:String, val doc:Usuario)
class LoginResponse(var user:LoginData ,var token:String)
class ResetPasswordResponse(var email:String,var estado:String)
class ResetUserEmail(var email:String)