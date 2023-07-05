package com.example.auto_gestion_v4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.auto_gestion_v4.databinding.ActivityDeleteBinding
import com.google.firebase.database.*

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().reference

        binding.deleteButton.setOnClickListener {
            val articleName = binding.deleteName.text.toString()
            if (articleName.isNotEmpty()) {
                // Chercher l'article dans la base de données
                database.child("Accessories").orderByChild("dataTitle").equalTo(articleName).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (articleSnapshot in snapshot.children) {
                            // Supprimer l'article de la base de données
                            articleSnapshot.ref.removeValue().addOnSuccessListener {
                                Toast.makeText(this@DeleteActivity, "L'article est supprimé", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this@DeleteActivity, "L'article n'a pas été supprimé", Toast.LENGTH_SHORT).show()
                            }
                        }
                        binding.deleteName.text.clear()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@DeleteActivity, "Erreur de la base de données", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Entrez le nom de l'article", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
