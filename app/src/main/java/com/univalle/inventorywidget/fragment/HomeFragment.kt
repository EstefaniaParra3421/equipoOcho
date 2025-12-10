package com.univalle.inventorywidget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentHomeBinding

/**
 * Fragment principal que muestra la lista de productos del inventario
 * Permite navegar a otros fragmentos para agregar o ver detalles
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        // Navegar al fragmento de detalles
        binding.btnGoToDetail.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }

        // Navegar al fragmento de agregar producto con datos
        binding.btnAddProduct.setOnClickListener {
            val productName = binding.etProductName.text.toString()
            val bundle = Bundle().apply {
                putString("productName", productName)
            }
            findNavController().navigate(R.id.action_homeFragment_to_addFragment, bundle)
        }

        // Navegar directamente al fragmento de agregar
        binding.btnGoToAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }
}

