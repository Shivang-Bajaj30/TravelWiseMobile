package com.example.travelwise.data

import com.example.travelwise.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class FirebaseUserRepository : UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun signUp(email: String, password: String, fullName: String, phone: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    return@continueWithTask task
                }
                val uid = task.result.user?.uid
                    ?: return@continueWithTask Tasks.forException(IllegalStateException("UID is null"))

                // Save profile using UID as document ID
                val userData = hashMapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "phone" to phone
                )
                firestore.collection("users").document(uid).set(userData)
                    .continueWithTask { firestoreTask ->
                        if (firestoreTask.isSuccessful) task else firestoreTask.exception?.let { Tasks.forException(it) } ?: task
                    }
            }
    }

    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun getUserProfile(uid: String): Task<User?> {
        return firestore.collection("users").document(uid).get().continueWith { task ->
            val doc = task.result
            if (task.isSuccessful && doc != null && doc.exists()) {
                User(
                    id = 0,
                    fullName = doc.getString("fullName") ?: "",
                    email = doc.getString("email") ?: "",
                    phone = doc.getString("phone") ?: "",
                    password = "" // Never store password
                )
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

    // ADD THIS: Delete user from Auth + Firestore
    fun deleteCurrentUser(): Task<Void> {
        val user = auth.currentUser ?: return Tasks.forException(IllegalStateException("No user logged in"))

        return user.delete().continueWithTask {
            firestore.collection("users").document(user.uid).delete()
        }
    }
}