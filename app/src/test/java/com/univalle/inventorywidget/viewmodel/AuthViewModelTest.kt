package com.univalle.inventorywidget.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.data.model.UserRequest
import com.univalle.inventorywidget.data.model.UserResponse
import com.univalle.inventorywidget.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var authRepository: FirebaseAuthRepository
    private lateinit var authViewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        authViewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registerUser should call repository registerUser`() = runTest {
        // Given
        val userRequest = UserRequest("test@example.com", "password123")

        // When
        authViewModel.registerUser(userRequest)
        advanceUntilIdle()

        // Then
        verify(authRepository).registerUser(any(), any())
    }

    @Test
    fun `loginUser should call repository loginUser and update loginResult`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        // When
        authViewModel.loginUser(email, password)
        advanceUntilIdle()

        // Then
        verify(authRepository).loginUser(eq(email), eq(password), any())
    }

    @Test
    fun `logOut should call repository logOut and clear currentUserEmail`() {
        // Given
        // Establecer un email inicial
        authViewModel.checkSession()

        // When
        authViewModel.logOut()

        // Then
        verify(authRepository).logOut()
        assertNull(authViewModel.currentUserEmail.value)
    }

    @Test
    fun `checkSession should update currentUserEmail when user is logged in`() {
        // Given
        val expectedEmail = "test@example.com"
        whenever(authRepository.isUserLoggedIn()).thenReturn(true)
        whenever(authRepository.getCurrentUserEmail()).thenReturn(expectedEmail)

        // When
        authViewModel.checkSession()

        // Then
        verify(authRepository).isUserLoggedIn()
        verify(authRepository).getCurrentUserEmail()
        assertEquals(expectedEmail, authViewModel.currentUserEmail.value)
    }

    @Test
    fun `getCurrentUserEmail should return email from repository`() {
        // Given
        val expectedEmail = "test@example.com"
        whenever(authRepository.getCurrentUserEmail()).thenReturn(expectedEmail)

        // When
        val result = authViewModel.getCurrentUserEmail()

        // Then
        verify(authRepository).getCurrentUserEmail()
        assertEquals(expectedEmail, result)
    }
}
