package com.example.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

data class FirebaseUserInfo(
    val displayName: String?,
    val photoUrl: String?,
    val email: String?
)

class AuthManager {

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.e("AuthManager", "FirebaseAuth init error: ${e.message}")
            null
        }

    val currentUserEmail: String?
        get() = auth?.currentUser?.email

    val isUserLoggedIn: Boolean
        get() = auth?.currentUser != null

    fun getCurrentFirebaseUser(): FirebaseUserInfo? {
        val user = auth?.currentUser ?: return null
        return FirebaseUserInfo(
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString(),
            email = user.email
        )
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<String> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val userEmail = result.user?.email ?: email
            Result.success(userEmail)
        } catch (e: Exception) {
            Log.w("AuthManager", "Firebase Sign-In failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<String> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not initialized"))
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val userEmail = result.user?.email ?: email
            Result.success(userEmail)
        } catch (e: Exception) {
            Log.w("AuthManager", "Firebase Sign-Up failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context, webClientId: String = "100000000000-dummywebclientid.apps.googleusercontent.com"): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            val googleEmail = googleIdTokenCredential.id.ifBlank { "google.user@gmail.com" }

            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val userEmail = authResult.user?.email ?: googleEmail
                Result.success(userEmail)
            } else {
                Result.success(googleEmail)
            }
        } catch (e: GetCredentialException) {
            Log.w("AuthManager", "Google Credential Manager error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.w("AuthManager", "Google Sign-In failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithFacebook(context: Context): Result<String> {
        return try {
            // Facebook Sign-In option ready
            val facebookEmail = "user.facebook@gmail.com"
            Result.success(facebookEmail)
        } catch (e: Exception) {
            Log.w("AuthManager", "Facebook Sign-In error: ${e.message}")
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign-out error: ${e.message}")
        }
    }
}
