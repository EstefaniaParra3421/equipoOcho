package com.univalle.inventorywidget.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.inventorywidget.LoginActivityWithFirebase
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.adapter.ProductAdapter
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.databinding.FragmentHomeInventoryBinding
import com.univalle.inventorywidget.viewmodel.AuthViewModel
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fragment principal que muestra la lista de productos del inventario
 * HU 3.0: Ventana Home Inventario
 */
@AndroidEntryPoint
class HomeInventoryFragment : Fragment() {

    private var _binding: FragmentHomeInventoryBinding? = null
    private val binding get() = _binding!!

    // ViewModels compartidos con la Activity
    private val productViewModel: ProductViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupToolbar()
        setupFab()
        setupBackButton()
        observeProducts()
        
        // Cargar productos al iniciar
        loadProducts()
    }
    

    /**
     * Configura el RecyclerView con el adapter
     */
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Criterio 8: Click en item -> HU 5.0 (Detalle del producto)
            val bundle = Bundle().apply {
                putInt("productId", product.id)
            }
            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_productDetailFragment,
                bundle
            )
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    /**
     * Configura el toolbar y el botón de logout (igual que en Compose)
     */
    private fun setupToolbar() {
        // Criterio 3: Logout redirige al login (igual que IconButton en Compose)
        binding.btnLogout.setOnClickListener {
            authViewModel.logOut()
            
            val intent = Intent(requireContext(), LoginActivityWithFirebase::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    /**
     * Configura el FloatingActionButton
     */
    private fun setupFab() {
        // Criterio 7: FAB -> HU 4.0 (Agregar Producto)
        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addProductFragment)
        }
    }

    /**
     * Criterio 4: Botón atrás -> envía la app al escritorio (background)
     */
    private fun setupBackButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * Observa los cambios en la lista de productos
     */
    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.products.collect { products ->
                updateUI(products)
            }
        }
    }

    /**
     * Actualiza la UI según el estado de los productos
     */
    private fun updateUI(products: List<Product>) {
        // Ocultar el progress bar
        binding.progressBar.visibility = View.GONE
        
        if (products.isEmpty()) {
            // Mostrar estado vacío
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvProducts.visibility = View.GONE
        } else {
            // Mostrar lista de productos
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvProducts.visibility = View.VISIBLE
            productAdapter.submitList(products)
        }
    }

    /**
     * Carga los productos desde Firestore
     */
    private fun loadProducts() {
        // Criterio 6: Mostrar progress circular naranja mientras carga
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmptyState.visibility = View.GONE
        binding.rvProducts.visibility = View.GONE
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                productViewModel.loadProducts()
                
                // Esperar un momento para asegurar que se cargaron los datos
                delay(1500)
                
                // Si después de cargar sigue sin productos, actualizar UI
                val currentProducts = productViewModel.products.value
                updateUI(currentProducts)
            } catch (e: Exception) {
                // En caso de error, mostrar estado vacío
                binding.progressBar.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

