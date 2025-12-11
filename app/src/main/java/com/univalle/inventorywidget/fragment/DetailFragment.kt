package com.univalle.inventorywidget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentDetailBinding
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Fragment que muestra los detalles de un producto seleccionado
 * HU 5.0: Ventana Detalle del Producto
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel compartido con la Activity
    private val productViewModel: ProductViewModel by activityViewModels()
    
    // Formato de número colombiano (HU 1.0)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupDeleteButton()
        setupFab()
        loadProductData()
    }

    /**
     * Configura el toolbar con la flecha de atrás
     * Criterio 1: Flecha de atrás -> HU 3.0 (Home Inventario)
     */
    private fun setupToolbar() {
        // Configurar el toolbar como ActionBar para que la flecha funcione
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Manejar el click en la flecha de atrás
        binding.toolbar.setNavigationOnClickListener {
            // Criterio 1: Navegar a HU 3.0 (Home Inventario)
            findNavController().navigate(R.id.action_productDetailFragment_to_homeInventoryFragment)
        }
    }

    /**
     * Carga los datos del producto desde los argumentos y los muestra en la tarjeta
     * Criterio 2: Mostrar nombre, precio unidad, cantidad disponible y total
     */
    private fun loadProductData() {
        val productId = arguments?.getInt("productId", -1) ?: -1
        
        if (productId == -1) {
            // No hay producto seleccionado, volver al home
            findNavController().navigate(R.id.action_productDetailFragment_to_homeInventoryFragment)
            return
        }
        
        // Observar la lista de productos para encontrar el producto seleccionado
        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.products.collect { products ->
                val product = products.find { it.id == productId }
                
                if (product != null) {
                    // Criterio 2: Mostrar datos del producto en la tarjeta
                    displayProductData(product)
                } else {
                    // Producto no encontrado, volver al home
                    findNavController().navigate(R.id.action_productDetailFragment_to_homeInventoryFragment)
                }
            }
        }
    }
    
    /**
     * Muestra los datos del producto en la tarjeta
     * Criterio 2: Nombre, precio unidad, cantidad disponible y total (precio × cantidad)
     */
    private fun displayProductData(product: com.univalle.inventorywidget.data.model.Product) {
        // Nombre del producto
        binding.tvProductName.text = product.name
        
        // Precio de la unidad (formato colombiano)
        binding.tvProductPrice.text = "$ ${numberFormat.format(product.price)}"
        
        // Cantidad disponible
        binding.tvProductQuantity.text = product.quantity.toString()
        
        // Criterio 2: Total = precio × cantidad
        val total = product.price * product.quantity
        binding.tvProductTotal.text = "$ ${numberFormat.format(total)}"
    }
    
    /**
     * Configura el botón de eliminar
     * Criterio 3: Eliminar producto de Firestore y redirigir a Home Inventario
     */
    private fun setupDeleteButton() {
        binding.btnDeleteProduct.setOnClickListener {
            val productId = arguments?.getInt("productId", -1) ?: -1
            
            if (productId == -1) {
                Toast.makeText(context, "Error: Producto no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Criterio 3: Eliminar producto de Firestore
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    productViewModel.deleteProduct(productId)
                    
                    Toast.makeText(
                        context,
                        "Producto eliminado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Criterio 3: Redirigir a Home Inventario
                    // La lista se actualizará automáticamente porque usa StateFlow
                    findNavController().navigate(R.id.action_productDetailFragment_to_homeInventoryFragment)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error al eliminar el producto: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    /**
     * Configura el FAB para editar producto
     * Criterio 4: Navegar a HU 6.0 (Ventana Editar Producto)
     */
    private fun setupFab() {
        binding.fabEdit.setOnClickListener {
            val productId = arguments?.getInt("productId", -1) ?: -1
            
            if (productId == -1) {
                Toast.makeText(context, "Error: Producto no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Criterio 4: Navegar a HU 6.0 (Ventana Editar Producto)
            // Por ahora usamos AddFragment pasando el productId para modo edición
            val bundle = Bundle().apply {
                putInt("productId", productId)
                putBoolean("isEditMode", true)
            }
            findNavController().navigate(
                R.id.action_productDetailFragment_to_addProductFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

