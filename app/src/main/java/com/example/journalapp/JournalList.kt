package com.example.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journalapp.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class JournalList : AppCompatActivity() {

    //firebase reference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var storage: FirebaseStorage
    var db = FirebaseFirestore.getInstance()
    private lateinit var storageReference: StorageReference
    var collectionReference : CollectionReference = db.collection("Journal")

    private lateinit var journalList: MutableList<Journal>
    private lateinit var journalAdapter: JournalRecyclerViewAdapter
    private lateinit var noPostTextView: TextView
    private lateinit var binding: ActivityJournalListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
        setSupportActionBar(binding.toolbar) // Set the toolbar

        auth = Firebase.auth
        user = auth.currentUser!!

        // Initialize post ArrayList
        journalList = arrayListOf()
        journalAdapter = JournalRecyclerViewAdapter(this, journalList) // Initialize adapter
        binding.recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = journalAdapter
        }

        noPostTextView = binding.tvListNoPosts
    }

    override fun onStart() {
        super.onStart()

        journalList.clear() // Clear list before fetching new data

        collectionReference.whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val journal = document.toObject(Journal::class.java)
                        journalList.add(journal)
                    }
                    journalAdapter.notifyDataSetChanged() // Update RecyclerView
                    noPostTextView.visibility = View.INVISIBLE // Hide "No Posts" message
                } else {
                    noPostTextView.visibility = View.VISIBLE // Show "No Posts" message
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add -> {
                startActivity(Intent(this, AddJournalActivity::class.java))
                return true
            }
            R.id.sign_out ->  {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}



