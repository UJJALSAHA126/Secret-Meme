package com.example.secretmeme.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.secretmeme.R
import com.example.secretmeme.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("data", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")

        lifecycleScope.launch {
            delay(1500L)
            if (name == "") {
                findNavController().navigate(R.id.action_splash_to_username)
            } else {
                val action = SplashScreenFragmentDirections.actionSplashToGetContacts(name!!)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

}