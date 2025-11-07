package com.example.travelwise.data

import com.example.travelwise.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface UserRepository {
    fun signUp(email: String, password: String, fullName: String, phone: String): Task<AuthResult>
    fun login(email: String, password: String): Task<AuthResult>
    fun getUserProfile(uid: String): Task<User?>
    fun saveUserProfile(uid: String, user: User): Task<Void>
    fun userExistsByEmail(email: String): Task<Boolean>
}


