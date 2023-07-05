package com.example.auto_gestion_v4

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_gestion_v4.databinding.ActivityUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var imageURL: String? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data?.data
                binding.updateImage.setImageURI(uri)
            } else {
                Toast.makeText(this@UploadActivity, "Image non sélectionnée", Toast.LENGTH_SHORT).show()
            }
        }
        binding.updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.saveButton.setOnClickListener {
            saveData()
        }

        setupCategorySpinner()
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Sélectionnez une catégorie", "Tous", "Filtres", "Moteurs", "Pneus") // Remplacez par vos catégories réelles
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter
    }

    private fun saveData() {
        if (uri == null) {
            Toast.makeText(this@UploadActivity, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("Images")
            .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@UploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener { exception ->
            dialog.dismiss()
            Toast.makeText(this@UploadActivity, "Erreur lors du téléchargement de l'image : ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadData() {
        val title = binding.uploadTitle.text.toString()
        val desc = binding.uploadDesc.text.toString()
        val codeBar = binding.uploadCode.text.toString()
        val quantity = binding.uploadQuantity.text.toString().toIntOrNull() ?: 0
        val category = binding.categorySpinner.selectedItem.toString()
        val prix = binding.uploadPrix.text.toString().toDouble()

        val dataClass = DataClass(null, title, desc, codeBar, imageURL, category, quantity, prix )
        val accessoriesRef = FirebaseDatabase.getInstance().getReference("Accessories")
        val newAccessoriesRef = accessoriesRef.push() // Obtient une référence automatique avec un ID unique
        dataClass.dataId = newAccessoriesRef.key // Définit l'ID généré automatiquement pour la classe DataClass

        newAccessoriesRef.setValue(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@UploadActivity, "Enregistré", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this@UploadActivity, "Erreur lors de l'enregistrement des données : ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
