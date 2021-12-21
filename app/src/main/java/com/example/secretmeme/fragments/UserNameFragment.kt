package com.example.secretmeme.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.secretmeme.R
import com.example.secretmeme.databinding.FragmentUserNameBinding

class UserNameFragment : Fragment() {

    private var _binding: FragmentUserNameBinding? = null
    private val binding get() = _binding!!

    private val fromBottomAnim: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),
        R.anim.from_bottom) }
    private val fromTopAnim: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),
        R.anim.from_top) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserNameBinding.inflate(inflater, container, false)

        binding.nameTextLayout.startAnimation(fromBottomAnim)
        binding.goButton.startAnimation(fromTopAnim)

        binding.goButton.setOnClickListener {
            val name = binding.nameText.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Please Enter A Valid Name", Toast.LENGTH_SHORT)
                    .show()
                binding.nameText.setText("")
            } else {
                createSharedPreference(name)
                val action = UserNameFragmentDirections.actionUserNameToGetContacts(name)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    private fun createSharedPreference(name: String) {
        val sharedPreferences = requireContext().getSharedPreferences("data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.apply()
    }

}