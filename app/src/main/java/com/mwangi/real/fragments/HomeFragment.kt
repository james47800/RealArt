package com.mwangi.real.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.mwangi.real.*
import com.mwangi.real.adapters.ArtsAdapter
import com.mwangi.real.models.Arts
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var imageContainer: LinearLayout
    private lateinit var storageReference: StorageReference
    private lateinit var searchView: SearchView
    private lateinit var filteredArtsList: ArrayList<Arts>
    private lateinit var artsList : ListView
    private lateinit var arts : ArrayList<Arts>
    private lateinit var artsAdapter: ArtsAdapter

    companion object {
        private const val TAG = "HomeFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_display, container, false)

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Fetching art")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        searchView = view.findViewById(R.id.searchView)
        artsList = view.findViewById(R.id.artsList)
        arts = ArrayList()
        artsAdapter = ArtsAdapter(context!!,arts)

        var artsReference = FirebaseDatabase.getInstance().getReference().child("Arts")
        artsReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arts.clear()
                for (snap in snapshot.children){
                    var art = snap.getValue(Arts::class.java)
                    arts.add(art!!)
                }
                artsAdapter.notifyDataSetChanged()
                Collections.reverse(arts)
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "DB inaccessible", Toast.LENGTH_SHORT).show()
            }
        })
        artsList.adapter = artsAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return true
            }
        })
        return view
    }

    fun filter(text: String) {
        val temp = ArrayList<Arts>()
        for (d in arts) {
            if (d.userDescription.trim().toLowerCase()
                    .contains(text.trim { it <= ' ' }.lowercase(Locale.getDefault()))
            ) {
                temp.add(d)
            }
        }
        //update recyclerview
        artsAdapter.setSearchOperation(temp)
    }
}