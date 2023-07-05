package com.example.auto_gestion_v4

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.auto_gestion_v4.databinding.ActivityUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private var itemId: String? = null
    private var imageUrl: String? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemId = intent.getStringExtra("ItemId")
        imageUrl = intent.getStringExtra("Image")

        binding.updateTitle.setText(intent.getStringExtra("Nom"))
        binding.updateDesc.setText(intent.getStringExtra("Description"))
        binding.updateCode.setText(intent.getStringExtra("Code"))
        // Charger l'image avec Glide ou toute autre bibliothèque de chargement d'images
        // en utilisant l'URL de l'image
        Glide.with(this).load(imageUrl).into(binding.updateImage)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data?.data
                binding.updateImage.setImageURI(uri)
            } else {
                Toast.makeText(this@UpdateActivity, "Image non sélectionnée", Toast.LENGTH_SHORT).show()
            }
        }

        binding.updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        val categories = arrayOf("Sélectionnez une catégorie","Tous", "Filtres", "Moteurs", "Pneus")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        binding.updateCategorySpinner.adapter = adapter

        binding.modifieButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val title = binding.updateTitle.text.toString()
        val desc = binding.updateDesc.text.toString()
        val codeBar = binding.updateCode.text.toString()

        val storageReference = FirebaseStorage.getInstance().reference.child("Images")
            .child(uri?.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@UpdateActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        // Mettre à jour l'image si une nouvelle image a été sélectionnée
        if (uri != null) {
            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageUrl = urlImage.toString()
                updateData(title, desc, codeBar)
                dialog.dismiss()
            }.addOnFailureListener {
                dialog.dismiss()
            }
        } else {
            updateData(title, desc, codeBar)
            dialog.dismiss()
        }
    }

    private fun updateData(title: String, desc: String, codeBar: String) {
        val category = binding.updateCategorySpinner.selectedItem.toString()
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val quantity = binding.updateQuantity.text.toString().toIntOrNull() ?: 0
        val prix = binding.updatePrix.text.toString().toDouble()
        val accessoriesRef: DatabaseReference = database.getReference("Accessories").child(itemId!!)
        val updatedData = DataClass(itemId, title, desc, codeBar, imageUrl, category, quantity,prix)
        accessoriesRef.setValue(updatedData) { error, _ ->
            if (error == null) {
                Toast.makeText(this@UpdateActivity, "Modifié", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@UpdateActivity, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
