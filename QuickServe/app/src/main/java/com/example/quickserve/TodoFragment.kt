package com.example.quickserve

import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoFragment : Fragment() {
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_todo)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_task)
        todoAdapter = TodoAdapter(emptyList(), onComplete = { task ->
            tasksViewModel.update(task.copy(isCompleted = !task.isCompleted))
        }, onDelete = { task ->
            tasksViewModel.delete(task)
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = todoAdapter
        tasksViewModel.allTasks.observe(viewLifecycleOwner, Observer { tasks ->
            todoAdapter = TodoAdapter(tasks, onComplete = { task ->
                tasksViewModel.update(task.copy(isCompleted = !task.isCompleted))
            }, onDelete = { task ->
                tasksViewModel.delete(task)
            })
            recyclerView.adapter = todoAdapter
        })
        fab.setOnClickListener { showAddTaskDialog() }
        return view
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_task, null)
        val taskEdit = dialogView.findViewById<EditText>(R.id.edit_task_text)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val text = taskEdit.text.toString().trim()
                if (text.isNotEmpty()) {
                    tasksViewModel.insert(Task(text = text))
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
    }
} 