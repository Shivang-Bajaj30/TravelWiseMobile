package com.example.travelwise.data

import com.example.travelwise.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUserRepository : UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun signUp(email: String, password: String, fullName: String, phone: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun getUserProfile(uid: String): Task<User?> {
        return firestore.collection("users").document(uid).get().continueWith { task ->
            val doc = task.result
            if (task.isSuccessful && doc != null && doc.exists()) {
                val fullName = doc.getString("fullName") ?: ""
                val email = doc.getString("email") ?: ""
                val phone = doc.getString("phone") ?: ""
                val passwordPlaceholder = "" // Never store actual password
                User(id = 0, fullName = fullName, email = email, phone = phone, password = passwordPlaceholder)
            } else {
                null
            }
        }
    }

    override fun saveUserProfile(uid: String, user: User): Task<Void> {
        val data = hashMapOf(
            "fullName" to user.fullName,
            "email" to user.email,
            "phone" to user.phone
        )
        return firestore.collection("users").document(uid).set(data)
    }

    override fun userExistsByEmail(email: String): Task<Boolean> {
        return auth.fetchSignInMethodsForEmail(email).continueWith { task ->
            val methods = task.result?.signInMethods
            methods != null && methods.isNotEmpty()
        }
    }
}


