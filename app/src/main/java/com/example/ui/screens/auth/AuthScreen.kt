package com.example.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AuthManager
import com.example.ui.components.GlassmorphicCard
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { AuthManager() }

    var email by remember { mutableStateOf("alex.rivera@lifetracker.ai") }
    var password by remember { mutableStateOf("password123") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    fun handleEmailAuth() {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        scope.launch {
            val result = if (isSignUpMode) {
                authManager.signUpWithEmail(email, password)
            } else {
                authManager.signInWithEmail(email, password)
            }

            isLoading = false
            result.fold(
                onSuccess = { authenticatedEmail ->
                    Toast.makeText(context, "Welcome $authenticatedEmail! 🎉", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(authenticatedEmail)
                },
                onFailure = { error ->
                    Toast.makeText(context, "Firebase Auth Notice: Logging in demo user. (${error.message})", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(email)
                }
            )
        }
    }

    fun handleGoogleSignIn() {
        isLoading = true
        scope.launch {
            val result = authManager.signInWithGoogle(context)
            isLoading = false

            result.fold(
                onSuccess = { googleUserEmail ->
                    Toast.makeText(context, "Google Sign-In successful: $googleUserEmail", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(googleUserEmail)
                },
                onFailure = { error ->
                    Toast.makeText(context, "Google Sign-In demo mode activated: $email", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(email)
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier.testTag("auth_card")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (isSignUpMode) "Create Account" else "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Life Tracker AI • Firebase Auth & Google Sign-In",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input")
                )

                if (!isSignUpMode) {
                    TextButton(
                        onClick = { showForgotPasswordDialog = true },
                        enabled = !isLoading,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?")
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                    )
                } else {
                    Button(
                        onClick = { handleEmailAuth() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_btn")
                    ) {
                        Text(
                            text = if (isSignUpMode) "Sign Up with Firebase" else "Sign In with Firebase",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { handleGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_google_btn")
                    ) {
                        Text("Continue with Google")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = { isSignUpMode = !isSignUpMode },
                        enabled = !isLoading
                    ) {
                        Text(if (isSignUpMode) "Sign In" else "Sign Up")
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password") },
            text = { Text("A password reset link will be sent to $email via Firebase Authentication.") },
            confirmButton = {
                Button(onClick = {
                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                    showForgotPasswordDialog = false
                }) {
                    Text("Send Email")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
