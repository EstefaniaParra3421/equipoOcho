package com.univalle.inventorywidget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentAddBinding

/**
 * Fragment para agregar o editar productos en el inventario
 * Muestra un formulario con campos para introducir datos del producto
 */
class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        setupActions()
    }

    private fun loadData() {
        // Capturar datos que vienen del fragmento anterior
        val productName = arguments?.getString("productName")
        if (!productName.isNullOrEmpty()) {
            binding.etAddProductName.setText(productName)
            Toast.makeText(context, "Datos recibidos: $productName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActions() {
        // Guardar producto
        binding.btnSaveProduct.setOnClickListener {
            val name = binding.etAddProductName.text.toString()
            val quantity = binding.etProductQuantity.text.toString()
            
            if (name.isNotEmpty() && quantity.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "Producto guardado: $name - Cantidad: $quantity",
                    Toast.LENGTH_SHORT
                ).show()
                
                // Volver al home después de guardar
                findNavController().navigate(R.id.action_addFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar y volver
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }
    }
}

