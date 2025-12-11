package com.univalle.inventorywidget.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.databinding.FragmentEditProductBinding
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment para editar productos en el inventario
 * HU 6.0: Ventana Editar Producto
 */
@AndroidEntryPoint
class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel compartido con la Activity
    private val productViewModel: ProductViewModel by activityViewModels()
    
    private var currentProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        loadProductData()
        setupFieldValidation()
        setupActions()
    }

    /**
     * Configura el toolbar con la flecha de atrás
     * Criterio 1: Flecha de atrás -> HU 5.0 (Ventana Detalle del Producto)
     */
    private fun setupToolbar() {
        // Configurar el toolbar como ActionBar para que la flecha funcione
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Manejar el click en la flecha de atrás
        binding.toolbar.setNavigationOnClickListener {
            // Criterio 1: Navegar a HU 5.0 (Ventana Detalle del Producto)
            findNavController().navigateUp()
        }
    }

    /**
     * Carga los datos del producto desde los argumentos
     */
    private fun loadProductData() {
        val productId = arguments?.getInt("productId", -1) ?: -1
        
        if (productId == -1) {
            Toast.makeText(context, "Error: Producto no válido", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }
        
        // Observar la lista de productos para encontrar el producto seleccionado
        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.products.collect { products ->
                val product = products.find { it.id == productId }
                
                if (product != null) {
                    currentProduct = product
                    displayProductData(product)
                } else {
                    Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }
    
    /**
     * Muestra los datos del producto en los campos
     * Criterio 2: Mostrar código del producto (no editable)
     * Criterio 3: Mostrar información del producto en los 3 campos (Nombre, Precio, Cantidad)
     */
    private fun displayProductData(product: Product) {
        // Criterio 2: Mostrar código del producto (no editable)
        binding.tvProductCode.text = "Id: ${product.code}"
        
        // Criterio 3: Cargar datos del producto en los 3 campos editables
        // Campo 1: Nombre artículo
        binding.etProductName.setText(product.name)
        
        // Campo 2: Precio
        binding.etProductPrice.setText(product.price.toString())
        
        // Campo 3: Cantidad
        binding.etProductQuantity.setText(product.quantity.toString())
        
        // Criterio 5: Validar campos después de cargar los datos
        validateFields()
    }

    /**
     * Configura la validación de campos
     * Criterio 5: Validar en tiempo real cuando los campos cambian
     */
    private fun setupFieldValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Criterio 5: Validar campos en tiempo real
                validateFields()
            }
        }
        
        binding.etProductName.addTextChangedListener(textWatcher)
        binding.etProductPrice.addTextChangedListener(textWatcher)
        binding.etProductQuantity.addTextChangedListener(textWatcher)
    }
    
    /**
     * Valida que todos los campos estén llenos
     * Criterio 5: El botón queda inactivo si algún campo está vacío
     */
    private fun validateFields() {
        val name = binding.etProductName.text?.toString()?.trim() ?: ""
        val price = binding.etProductPrice.text?.toString()?.trim() ?: ""
        val quantity = binding.etProductQuantity.text?.toString()?.trim() ?: ""
        
        // Criterio 5: Habilitar botón solo si todos los campos están llenos
        val isEnabled = name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()
        binding.btnEditProduct.isEnabled = isEnabled
    }

    /**
     * Configura las acciones del botón Editar
     */
    private fun setupActions() {
        binding.btnEditProduct.setOnClickListener {
            val name = binding.etProductName.text?.toString()?.trim() ?: ""
            val priceText = binding.etProductPrice.text?.toString()?.trim() ?: ""
            val quantityText = binding.etProductQuantity.text?.toString()?.trim() ?: ""
            
            if (currentProduct == null) {
                Toast.makeText(context, "Error: Producto no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Deshabilitar el botón mientras se guarda
            binding.btnEditProduct.isEnabled = false
            
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Convertir y validar los valores numéricos
                    val priceDouble = priceText.toDoubleOrNull()
                    val quantityInt = quantityText.toIntOrNull()
                    
                    if (priceDouble == null || quantityInt == null) {
                        Toast.makeText(
                            requireContext(),
                            "Error: Valores numéricos inválidos",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnEditProduct.isEnabled = true
                        return@launch
                    }
                    
                    // Crear el producto actualizado
                    val updatedProduct = Product(
                        id = currentProduct!!.id,
                        code = currentProduct!!.code,
                        name = name,
                        price = priceDouble,
                        quantity = quantityInt
                    )
                    
                    // Criterio 4: Actualizar producto en Firestore
                    productViewModel.updateProduct(updatedProduct)
                    
                    Toast.makeText(
                        requireContext(),
                        "Producto actualizado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Criterio 4: Navegar a Home Inventario para mostrar el producto actualizado
                    // La lista se actualizará automáticamente porque usa StateFlow
                    findNavController().navigate(R.id.action_editProductFragment_to_homeInventoryFragment)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error al editar el producto: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnEditProduct.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

