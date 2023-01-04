package com.pixie.note7.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.api.NoteAdapter
import com.pixie.note7.api.Type
import com.pixie.note7.databinding.FragmentMainBinding
import com.pixie.note7.receiver.AlarmReceiver
import com.pixie.note7.weekdata.IdFinder

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


        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val listDividers = prefs.getBoolean("divider", true)
        if (listDividers){
            binding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }else{
            if (binding.recyclerView.itemDecorationCount > 0) binding.recyclerView.removeItemDecorationAt(0)
        }

        val darkMode = prefs.getBoolean("mode", true)
        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val deleteAllItems = prefs.getString("clear", "")
        if(deleteAllItems == "Yes"){
            deleteAll()
            val editor = prefs.edit()
            editor.putString("clear", "No")
            editor.apply()
        }

        act.loadNotes()
        nadapter?.notifyDataSetChanged()



    }

    private fun deleteAll() {

        act.noteList.forEach {
            if (it.getType() == Type.TODO){
                cancelAlarm(it.getId())
            }
            if(it.getType() == Type.IDEA){
                if (it.getAlarm().substring(0, 5) == "daily"){
                    cancelAlarm(it.getId())
                }else{
                    val id = IdFinder()
                    val shelby = id.getId("${it.getAlarm()}")
                    for (i in shelby){
                        cancelAlarm(i)
                    }
                }
            }
        }

        act.noteList.clear()
        act.saveNotes()
    }


    fun showNote(noteToShow: Int) {
        val action = MainFragmentDirections.actionMainFragmentToShowNoteFragment(noteToShow)
        findNavController().navigate(action)
    }

    private fun cancelAlarm(id: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Alarm is cancelled", Toast.LENGTH_SHORT).show()
    }

}