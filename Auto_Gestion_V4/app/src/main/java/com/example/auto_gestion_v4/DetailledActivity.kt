package com.example.auto_gestion_v4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.auto_gestion_v4.databinding.ActivityDetailledBinding

class DetailledActivity : AppCompatActivity() {
    var imageUrl = ""
    private lateinit var binding: ActivityDetailledBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailledBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        if (bundle != null) {
            binding.detailDesc.text = bundle.getString("Description")
            binding.detailTitle.text = bundle.getString("Nom")
            binding.detailPriority.text = bundle.getString("Code")
            binding.detailQuantite.text= bundle.getInt("quantite").toString()
            binding.detailPrix.text= bundle.getDouble("prix").toString()
            if (bundle.containsKey("Image")) {
                imageUrl = bundle.getString("Image").toString()
                Glide.with(this).load(imageUrl).into(binding.detailImage)
            }
        }

    }
}