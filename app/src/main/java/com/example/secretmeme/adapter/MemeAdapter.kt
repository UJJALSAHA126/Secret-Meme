package com.example.secretmeme.adapter

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.secretmeme.R
import com.example.secretmeme.databinding.CustomMemeLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MemeAdapter(val context: Context) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    val memeUrls = ArrayList<String>()

    inner class MemeViewHolder(binding: CustomMemeLayoutBinding ) : RecyclerView.ViewHolder(binding.root) {

        val memeImage: ImageView = binding.memeImage
        val layout: ConstraintLayout = binding.memeLayout
        val downloadButton: ImageView = binding.memeDownloadButton

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val binding = CustomMemeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {

        val currentImageUrl = memeUrls[position]

        GlobalScope.launch(Dispatchers.IO) {
            holder.memeImage.load(currentImageUrl)
        }

        holder.layout.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.popup_meme_lay)
            val image: ImageView = dialog.findViewById(R.id.popupMeme1)
            image.load(currentImageUrl)
            dialog.show()
        }

        holder.downloadButton.setOnClickListener {
            downloadMeme(currentImageUrl)
        }
    }

    override fun getItemCount(): Int {
        return memeUrls.size
    }

    private fun downloadMeme(url: String) {
        try {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setTitle("Meme Downloading Successful")
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + "Meme${System.currentTimeMillis() % 10000}.jpg"
                )
            downloadManager.enqueue(request)
            Toast.makeText(context, "Downloading ...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Failed :(", Toast.LENGTH_SHORT).show()
        }
    }
}