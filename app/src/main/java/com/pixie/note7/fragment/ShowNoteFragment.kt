package com.pixie.note7.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.api.Note
import com.pixie.note7.api.Type
import com.pixie.note7.databinding.FragmentShowNoteBinding
import com.pixie.note7.receiver.AlarmReceiver
import com.pixie.note7.weekdata.IdFinder

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ShowNoteFragment : Fragment() {

    private var _binding: FragmentShowNoteBinding? = null
    private val binding get() = _binding!!
    private val args: com.pixie.note7.fragment.ShowNoteFragmentArgs by navArgs()
    private lateinit var act: MainActivity
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = (activity as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentShowNoteBinding.inflate(inflater, container, false)

        note = act.noteList[args.num]
        binding.showTitle.text = note.getTitle()
        binding.showDescription.text = note.getDescription()
        binding.showType.text = note.getType().toString()
        binding.showAlarm.text = note.getAlarm()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("check","onViewCreated Fragment")



        binding.btnOK.setOnClickListener {
            findNavController().navigate(R.id.action_ShowNoteFragment_to_MainFragment)
        }

        binding.btnDelete.setOnClickListener {
            if(note.getAlarm() != "") {
                if (note.getType() == Type.TODO) {
                    cancelAlarm(note.getId())
                }
                if (note.getType() == Type.IDEA) {
                    if (note.getAlarm()?.substring(0, 5) == "daily") {
                        cancelAlarm(note.getId())
                    } else {
                        val id = IdFinder()
                        val shelby = id.getId(note.getAlarm())
                        for (i in shelby) {
                            cancelAlarm(i)
                        }
                    }
                }
            }
            deleteNote(args.num)
            findNavController().navigate(R.id.action_ShowNoteFragment_to_MainFragment)
        }

        binding.btnEdit.setOnClickListener {
            val action = ShowNoteFragmentDirections.actionShowNoteFragmentToEditNoteFragment(args.num)
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cancelAlarm(id: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Alarm is cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun deleteNote(noteToShow: Int){
        act.noteList.removeAt(noteToShow)
        act.saveNotes()
    }



}