package com.univalle.inventorywidget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentDetailBinding

/**
 * Fragment que muestra los detalles de un producto seleccionado
 * Permite editar o volver a la pantalla principal
 */
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupNavigation()
    }

    private fun setupUI() {
        // Capturar datos que vienen del fragmento anterior si los hay
        val productName = arguments?.getString("productName")
        if (!productName.isNullOrEmpty()) {
            binding.tvProductDetail.text = "Producto: $productName"
        }
    }

    private fun setupNavigation() {
        // Volver al home
        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
        }

        // Ir a editar producto
        binding.btnEditProduct.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_addFragment)
        }
    }
}

