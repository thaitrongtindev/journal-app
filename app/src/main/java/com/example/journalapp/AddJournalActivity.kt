package com.example.journalapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityAddJournalBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJournalBinding
    //credentials

    var currentUserId : String = ""
    var currentUsername : String = ""

    // firebase
    lateinit var auth : FirebaseAuth
    lateinit var user : FirebaseUser
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var storageReference: StorageReference
    var collectionReference  : CollectionReference = db.collection("Journal")
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_journal)

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)

        storageReference = FirebaseStorage.getInstance().getReference()

        auth = Firebase.auth

        binding.apply {
            progressBar.visibility = View.INVISIBLE
            if (JournalUser.instance != null) {
//                currentUserId = JournalUser.instance!!.userId.toString()
//                currentUsername = JournalUser.instance!!.username.toString()
                currentUserId = auth.currentUser?.uid.toString()
                currentUsername = auth.currentUser?.displayName.toString()

                postTitle.text = currentUsername
            }

            btnSave.setOnClickListener {
                saveJournal()
            }

            postImageView.setOnClickListener() {
                var i : Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*")
                startActivity(i)
            }
        }
    }

    private fun saveJournal() {
        var title : String = binding.edtPostTitle.text.toString()
        var throughts : String = binding.edtPostDescription.text.toString()
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(throughts) && imageUri != null) {
            // save path of image in storage
            var filePath: StorageReference = storageReference.child("journal_images")
                .child("my_image" + Timestamp.now())

            filePath.putFile(imageUri).addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener {
                    var imageUri : String = it.toString()

                    // create the object of journal
                    var timestamp: Timestamp = Timestamp(Date())

                    var journal : Journal = Journal(
                        title,
                        throughts,
                        imageUri,
                        currentUserId,
                        timestamp,
                        currentUsername
                    )

                    collectionReference.add(journal).addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        var i : Intent = Intent(this, JournalList::class.java)
                        startActivity(i)
                    }
                }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.INVISIBLE
                    }
            }
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 &&  resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.data!!
                binding.postImageView.setImageURI(imageUri)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if (auth != null) {

        }
    }
}