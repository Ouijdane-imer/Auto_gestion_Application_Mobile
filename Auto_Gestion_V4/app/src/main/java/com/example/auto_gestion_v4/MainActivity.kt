package com.example.auto_gestion_v4


import MyAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.auto_gestion_v4.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var databaseReference: DatabaseReference
    private var eventListener: ValueEventListener? = null
    private lateinit var categorySpinner: Spinner
    private var currentCategory: String = "Tous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val recyclerView = binding.recyclerView
        val gridLayoutManager = GridLayoutManager(this@MainActivity, 1)
        recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()

        dataList = ArrayList()
        adapter = MyAdapter(this@MainActivity, dataList)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Accessories")
        dialog.show()

        eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    dataClass?.let {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                try {
                    if (dialog != null && dialog.isShowing && !isFinishing) {
                        dialog.dismiss()
                    }
                } catch (e: Exception) {
                    // handle exception
                }
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

        // Set up search functionality
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        // Set up FAB click listener
        val fab = binding.fab
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        // Set up logout click listener
        val logout = binding.logout
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        categorySpinner = binding.categorySpinner
        val categories = arrayOf("Sélectionnez une catégorie","Tous", "Filtres", "Moteurs", "Pneus")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                currentCategory = selectedCategory
                filterListByCategory(currentCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
               //si n'est rien séléctionnée
            }
        }
    }

    private fun searchList(text: String) {
        val searchList = ArrayList<DataClass>()
        for (dataClass in dataList) {
            if (dataClass.dataTitle?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                if (currentCategory == "Tous" || dataClass.category == currentCategory) {
                    searchList.add(dataClass)
                }
            }
        }
        adapter.searchDataList(searchList)
    }

    private fun filterListByCategory(category: String) {
        val filteredList = ArrayList<DataClass>()
        for (dataClass in dataList) {
            if (category == "Tous" || dataClass.category == category) {
                filteredList.add(dataClass)
            }
        }
        adapter.searchDataList(filteredList)
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editText -> {
                // Handle edit action
                return true
            }
            R.id.delete -> {
                // Handle delete action
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_settings -> {
                 supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Settings()).commit()
            }

            R.id.nav_about -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, About()).commit()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Déconnexion!", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}









