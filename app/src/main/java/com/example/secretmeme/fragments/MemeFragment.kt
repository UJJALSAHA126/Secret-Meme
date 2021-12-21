package com.example.secretmeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.secretmeme.adapter.MemeAdapter
import com.example.secretmeme.databinding.FragmentMemeBinding
import com.example.secretmeme.mysingleton.MySingleTon
import kotlinx.coroutines.launch

class MemeFragment : Fragment() {

    private var _binding: FragmentMemeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MemeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private val url: String = "https://meme-api.herokuapp.com/gimme"
    private var lastUrl: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMemeBinding.inflate(inflater, container, false)

        adapter = MemeAdapter(requireContext())
        recyclerView = binding.recyclerView
        progressBar = binding.layOutProgressBar
        swipeRefresh = binding.swipeRefresh

        recyclerView.adapter = adapter
        getUrls(1, 50)

        swipeRefresh.setOnRefreshListener {
            getUrls(0, 30)
            swipeRefresh.isRefreshing = false
        }

        return binding.root
    }

    private fun getUrls(mode: Int, num: Int) {
        var count = 0
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            for (i in 0..num) {
//                val jsonRequestQueue = Volley.newRequestQueue(context)
                val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
                    val str = it.getString("url")
                    if (!adapter.memeUrls.contains(str)) {
                        lastUrl = str
                        if (mode == 1) {
                            adapter.memeUrls.add(str)
                        } else {
                            adapter.memeUrls.add(0, str)
                        }
                        adapter.notifyDataSetChanged()
                        progressBar.visibility = View.GONE
                    }
                }, {
                    Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
                })
//                jsonRequestQueue.add(jsonObjectRequest)
                MySingleTon.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
            }
        }
    }
}