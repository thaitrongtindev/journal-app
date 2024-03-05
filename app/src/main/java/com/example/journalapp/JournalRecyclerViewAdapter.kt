package com.example.journalapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.journalapp.databinding.JournalRowBinding

class JournalRecyclerViewAdapter(val context: Context, val journalList: List<Journal>)

    : RecyclerView.Adapter<JournalRecyclerViewAdapter.JournalViewHolder>() {

    private lateinit var binding: JournalRowBinding

        class JournalViewHolder(val binding : JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(journal : Journal) {

                binding.journal = journal

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
       binding = JournalRowBinding.inflate(LayoutInflater.from(parent.context),
           parent, false)

        return JournalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
       val journal = journalList[position]
        holder.bind(journal)
    }
}