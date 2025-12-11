package com.univalle.inventorywidget.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var productViewModel: ProductViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var productsFlow: MutableStateFlow<List<Product>>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = mock()
        productsFlow = MutableStateFlow(emptyList())
        whenever(productRepository.products).thenReturn(productsFlow)
        productViewModel = ProductViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertProduct should call repository insert`() = runTest {
        // Given
        val product = Product(
            id = 1,
            code = 100,
            name = "Producto Test",
            price = 10.0,
            quantity = 5
        )

        // When
        productViewModel.insertProduct(product)
        advanceUntilIdle()

        // Then
        verify(productRepository).insert(product)
    }

    @Test
    fun `loadProducts should call repository refreshProducts`() = runTest {
        // When
        productViewModel.loadProducts()
        advanceUntilIdle()

        // Then
        verify(productRepository).refreshProducts()
    }

    @Test
    fun `updateProduct should call repository update`() = runTest {
        // Given
        val product = Product(
            id = 1,
            code = 100,
            name = "Producto Actualizado",
            price = 15.0,
            quantity = 10
        )

        // When
        productViewModel.updateProduct(product)
        advanceUntilIdle()

        // Then
        verify(productRepository).update(product)
    }

    @Test
    fun `deleteProduct should call repository delete`() = runTest {
        // Given
        val productId = 1

        // When
        productViewModel.deleteProduct(productId)
        advanceUntilIdle()

        // Then
        verify(productRepository).delete(productId)
    }

    @Test
    fun `products should return StateFlow from repository`() {
        // Given
        val expectedProducts = listOf(
            Product(1, 100, "Producto 1", 10.0, 5),
            Product(2, 200, "Producto 2", 20.0, 10)
        )
        productsFlow.value = expectedProducts

        // When
        val result = productViewModel.products.value

        // Then
        assertEquals(expectedProducts, result)
        assertEquals(2, result.size)
    }
}
