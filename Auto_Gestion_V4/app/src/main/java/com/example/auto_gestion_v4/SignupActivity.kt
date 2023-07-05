package com.example.auto_gestion_v4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.example.auto_gestion_v4.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.showPasswordButton.setOnCheckedChangeListener { _, isChecked ->
            isPasswordVisible = isChecked
            togglePasswordVisibility(binding.signupPassword, isChecked)
            togglePasswordVisibility(binding.signupConfirm, isChecked)
        }

        binding.showPasswordButton.isChecked = isPasswordVisible

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    // Vérifier le format de l'e-mail
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // L'utilisateur est créé avec succès
                                    // Firebase enverra automatiquement un e-mail de confirmation

                                    firebaseAuth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificationTask ->
                                            if (verificationTask.isSuccessful) {
                                                // Actualiser l'état de l'utilisateur dans Firebase Auth
                                                firebaseAuth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                                                    if (reloadTask.isSuccessful) {
                                                        // L'utilisateur a été rechargé avec succès, vous pouvez maintenant autoriser la connexion
                                                        Toast.makeText(
                                                            this,
                                                            "Inscription réussie. Veuillez vérifier votre adresse e-mail.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        val intent = Intent(this, LoginActivity::class.java)
                                                        startActivity(intent)
                                                    } else {
                                                        // Une erreur s'est produite lors du rechargement de l'utilisateur
                                                        Toast.makeText(
                                                            this,
                                                            "Erreur lors du rechargement de l'utilisateur. Veuillez réessayer.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            } else {
                                                // Une erreur s'est produite lors de l'envoi de l'e-mail de confirmation
                                                Toast.makeText(
                                                    this,
                                                    "Erreur lors de l'envoi de l'e-mail de confirmation. Veuillez réessayer.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    // Une erreur s'est produite lors de la création de l'utilisateur
                                    Toast.makeText(
                                        this,
                                        "Erreur lors de l'inscription. Veuillez réessayer.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Veuillez entrer un e-mail valide.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Le mot de passe ne correspond pas.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Les champs ne peuvent pas être vides.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
    private fun togglePasswordVisibility(editText: EditText, isPasswordVisible: Boolean) {
        if (isPasswordVisible) {
            // Afficher le texte en clair
            editText.transformationMethod = null
        } else {
            // Masquer le texte
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        // Déplacer le curseur à la fin du texte
        editText.setSelection(editText.text.length)
    }

}
