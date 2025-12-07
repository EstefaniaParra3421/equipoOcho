package com.univalle.inventorywidget.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.univalle.inventorywidget.HomeActivity
import com.univalle.inventorywidget.ui.home.HomeInventoryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // Navegamos a la HomeActivity real
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            )
        }
    }
}
