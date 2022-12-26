package com.pixie.note7.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.api.NoteAdapter
import com.pixie.note7.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    var nadapter: NoteAdapter? = null
    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = (activity as MainActivity)
        nadapter = NoteAdapter(this, act.noteList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = nadapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_NewNoteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        act.saveNotes()
    }

    override fun onResume() {
        super.onResume()

        act.loadNotes()
        nadapter?.notifyDataSetChanged()

//        val prefs = getSharedPreferences("Note settings", Context.MODE_PRIVATE)
//        val listDividers = prefs.getBoolean("dividers", true)
//        if (listDividers){
//            recyclerView!!.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
//        }else{
//            if (recyclerView!!.itemDecorationCount > 0) recyclerView!!.removeItemDecorationAt(0)
//        }
    }


    fun showNote(noteToShow: Int) {
        val action = MainFragmentDirections.actionMainFragmentToShowNoteFragment(noteToShow)
        findNavController().navigate(action)
    }

}