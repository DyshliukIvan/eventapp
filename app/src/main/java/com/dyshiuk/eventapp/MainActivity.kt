package com.dyshiuk.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.dyshiuk.eventapp.auth.TokenStorage
import com.dyshiuk.eventapp.navigation.AppNavigation
import com.dyshiuk.eventapp.network.EventDto
import com.dyshiuk.eventapp.network.FakeLoginRequest
import com.dyshiuk.eventapp.network.RetrofitClient
import com.dyshiuk.eventapp.screens.LoadingScreen
import com.dyshiuk.eventapp.screens.ErrorScreen
import com.dyshiuk.eventapp.ui.theme.EventAppTheme
import kotlinx.coroutines.launch
import com.dyshiuk.eventapp.auth.GoogleSignInHelper
import com.dyshiuk.eventapp.network.GoogleLoginRequest

class MainActivity : ComponentActivity() {

    private lateinit var tokenStorage: TokenStorage

    private var isLoggedIn by mutableStateOf(false)
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var events by mutableStateOf<List<EventDto>>(emptyList())

    private lateinit var googleSignInHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenStorage = TokenStorage(this)
        googleSignInHelper = GoogleSignInHelper(this)

        setContent {
            EventAppTheme {
                when {
                    isLoading -> {
                        LoadingScreen()
                    }

                    errorMessage != null -> {
                        ErrorScreen(
                            message = errorMessage!!,
                            onRetryClick = {
                                errorMessage = null
                                if (tokenStorage.getToken() != null) {
                                    tryAutoLogin()
                                } else {
                                    performLogin()
                                }
                            }
                        )
                    }

                    else -> {
                        AppNavigation(
                            isLoggedIn = isLoggedIn,
                            events = events,
                            onLoginClick = { performLogin() },
                            onLogoutClick = { logout() }
                        )
                    }
                }
            }
        }

        tryAutoLogin()
    }

    private fun tryAutoLogin() {
        val token = tokenStorage.getToken() ?: return

        isLoading = true
        errorMessage = null

        lifecycleScope.launch {
            try {
                RetrofitClient.api.getCurrentUser("Bearer $token")
                events = RetrofitClient.api.getEvents()
                isLoggedIn = true
            } catch (e: Exception) {
                tokenStorage.clearToken()
                isLoggedIn = false
                errorMessage = "Auto login failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun performLogin() {
        isLoading = true
        errorMessage = null

        lifecycleScope.launch {
            try {
                val googleIdToken = googleSignInHelper.signIn(
                    webClientId = "830319667435-2d7s2ncq99ck0jvit6bqh5tk9dni24qi.apps.googleusercontent.com"                )

                val loginResponse = RetrofitClient.api.googleLogin(
                    GoogleLoginRequest(idToken = googleIdToken)
                )

                tokenStorage.saveToken(loginResponse.accessToken)

                RetrofitClient.api.getCurrentUser(
                    "Bearer ${loginResponse.accessToken}"
                )

                events = RetrofitClient.api.getEvents()
                isLoggedIn = true

            } catch (e: Exception) {
                errorMessage = "Google login failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun logout() {
        tokenStorage.clearToken()
        isLoggedIn = false
        events = emptyList()
        errorMessage = null
    }
}