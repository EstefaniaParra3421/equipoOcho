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
import com.univalle.inventorywidget.databinding.FragmentAddBinding
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment para agregar productos en el inventario
 * HU 4.0: Ventana Agregar Producto
 */
@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel compartido con la Activity para guardar productos
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupCodeField()
        setupFieldValidation()
        setupActions()
    }
    
    /**
     * Configura el campo de código (Criterio 2)
     * - Solo números
     * - Máximo 4 dígitos
     */
    private fun setupCodeField() {
        // Agregar filtro para solo permitir números
        binding.etProductCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                // Asegurar que solo hay números
                val text = s.toString()
                if (text.isNotEmpty() && !text.all { it.isDigit() }) {
                    val filtered = text.filter { it.isDigit() }
                    binding.etProductCode.setText(filtered)
                    binding.etProductCode.setSelection(filtered.length)
                }
                validateFields()
            }
        })
    }
    
    /**
     * Configura validación en tiempo real de todos los campos
     * Criterio 6: Botón inactivo mientras algún campo esté vacío
     */
    private fun setupFieldValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        }
        
        // Agregar validación a todos los campos
        binding.etAddProductName.addTextChangedListener(textWatcher)
        binding.etProductQuantity.addTextChangedListener(textWatcher)
        binding.etProductPrice.addTextChangedListener(textWatcher)
    }
    
    /**
     * Valida que todos los campos estén llenos y habilita/deshabilita el botón
     */
    private fun validateFields() {
        val code = binding.etProductCode.text.toString().trim()
        val name = binding.etAddProductName.text.toString().trim()
        val quantity = binding.etProductQuantity.text.toString().trim()
        val price = binding.etProductPrice.text.toString().trim()
        
        val allFieldsFilled = code.isNotEmpty() && 
                             name.isNotEmpty() && 
                             quantity.isNotEmpty() && 
                             price.isNotEmpty()
        
        binding.btnSaveProduct.isEnabled = allFieldsFilled
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
            findNavController().navigate(R.id.action_addProductFragment_to_homeInventoryFragment)
        }
    }

    private fun setupActions() {
        // Guardar producto (Criterio 8)
        binding.btnSaveProduct.setOnClickListener {
            val code = binding.etProductCode.text.toString().trim()
            val name = binding.etAddProductName.text.toString().trim()
            val quantity = binding.etProductQuantity.text.toString().trim()
            val price = binding.etProductPrice.text.toString().trim()
            
            // Validación adicional (aunque el botón ya está deshabilitado si falta algo)
            if (code.isNotEmpty() && name.isNotEmpty() && quantity.isNotEmpty() && price.isNotEmpty()) {
                // Deshabilitar el botón mientras se guarda
                binding.btnSaveProduct.isEnabled = false
                
                // Guardar producto en Firestore (Criterio 8)
                lifecycleScope.launch {
                    try {
                        // Convertir y validar los valores numéricos
                        val codeInt = code.toIntOrNull()
                        val quantityInt = quantity.toIntOrNull()
                        val priceDouble = price.toDoubleOrNull()
                        
                        if (codeInt == null || quantityInt == null || priceDouble == null) {
                            Toast.makeText(
                                requireContext(),
                                "Error: Valores numéricos inválidos",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btnSaveProduct.isEnabled = true
                            return@launch
                        }
                        
                        // Crear el producto con los datos del formulario
                        val product = Product(
                            id = 0, // Firestore generará el ID automáticamente
                            code = codeInt,
                            name = name,
                            price = priceDouble,
                            quantity = quantityInt
                        )
                        
                        // Guardar producto en Firestore
                        productViewModel.insertProduct(product)
                        
                        // Limpiar los campos
                        binding.etProductCode.setText("")
                        binding.etAddProductName.setText("")
                        binding.etProductQuantity.setText("")
                        binding.etProductPrice.setText("")
                        
                        Toast.makeText(
                            requireContext(),
                            "Producto guardado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Volver al home después de guardar
                        // La lista se actualizará automáticamente porque usa StateFlow
                        if (isAdded && view != null) {
                            findNavController().navigate(R.id.action_addProductFragment_to_homeInventoryFragment)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Error al guardar el producto: ${e.message ?: "Error desconocido"}",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.btnSaveProduct.isEnabled = true
                    }
                }
            }
        }

        // Cancelar y volver
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addProductFragment_to_homeInventoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

