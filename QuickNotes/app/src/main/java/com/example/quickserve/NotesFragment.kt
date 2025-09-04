package com.example.quickserve

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesFragment : Fragment() {
    private val notesViewModel: NotesViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_notes)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_note)
        val searchView = view.findViewById<SearchView?>(R.id.search_notes)
        notesAdapter = NotesAdapter(emptyList(), onEdit = { note -> showAddEditDialog(note) }, onDelete = { note -> notesViewModel.delete(note) })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = notesAdapter
        notesViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            notesAdapter = NotesAdapter(notes, onEdit = { note -> showAddEditDialog(note) }, onDelete = { note -> notesViewModel.delete(note) })
            recyclerView.adapter = notesAdapter
        })
        fab.setOnClickListener { showAddEditDialog(null) }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    notesViewModel.search(it).observe(viewLifecycleOwner, Observer { notes ->
                        notesAdapter = NotesAdapter(notes, onEdit = { note -> showAddEditDialog(note) }, onDelete = { note -> notesViewModel.delete(note) })
                        recyclerView.adapter = notesAdapter
                    })
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    notesViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
                        notesAdapter = NotesAdapter(notes, onEdit = { note -> showAddEditDialog(note) }, onDelete = { note -> notesViewModel.delete(note) })
                        recyclerView.adapter = notesAdapter
                    })
                }
                return true
            }
        })
        return view
    }

    private fun showAddEditDialog(note: Note?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_note, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.edit_note_title)
        val descEdit = dialogView.findViewById<EditText>(R.id.edit_note_description)
        if (note != null) {
            titleEdit.setText(note.title)
            descEdit.setText(note.description)
        }
        val dialog = AlertDialog.Builder(context)
            .setTitle(if (note == null) "Add Note" else "Edit Note")
            .setView(dialogView)
            .setPositiveButton(if (note == null) "Add" else "Update") { _, _ ->
                val title = titleEdit.text.toString().trim()
                val desc = descEdit.text.toString().trim()
                if (title.isNotEmpty() || desc.isNotEmpty()) {
                    if (note == null) {
                        notesViewModel.insert(Note(title = title, description = desc))
                    } else {
                        notesViewModel.update(note.copy(title = title, description = desc))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
    }
} 