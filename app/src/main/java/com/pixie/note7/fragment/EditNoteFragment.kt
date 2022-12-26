package com.pixie.note7.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.alert.CustomDialog
import com.pixie.note7.alert.ResponseType
import com.pixie.note7.api.Note
import com.pixie.note7.api.Note.Companion.LUKAKU
import com.pixie.note7.api.NoteAdapter
import com.pixie.note7.api.Type
import com.pixie.note7.databinding.FragmentEditNoteBinding
import com.pixie.note7.receiver.AlarmReceiver
import com.pixie.note7.weekdata.Collect
import com.pixie.note7.weekdata.IdFinder
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private val args: com.pixie.note7.fragment.EditNoteFragmentArgs by navArgs()
    private lateinit var act: MainActivity
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = (activity as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)

        note = act.noteList[args.num]
        binding.editEditTitle.setText(note.getTitle())
        binding.editEditDescription.setText(note.getDescription())
        binding.editNoteType.text = note.getType().toString()
        binding.editTextAlarm.text = note.getAlarm()
        if(note.getType() == Type.NOTE){
            binding.editBtnAlarm.visibility = View.GONE
            binding.editImageView.visibility = View.GONE
            binding.editTextAlarm.visibility = View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            editBtnSave.setOnClickListener {
                if (!(editEditTitle.text.toString().trim() == ""
                            || editEditDescription.text.toString().trim() == "")) {

                    note.setTitle(editEditTitle.text.toString())
                    note.setDescription(editEditDescription.text.toString())

                    val action = EditNoteFragmentDirections.actionEditNoteFragmentToShowNoteFragment(args.num)
                    findNavController().navigate(action)
                }
            }

            var open = true
            editBtnAlarm.setOnClickListener {
                if(open){
                    if(note.getType() == Type.TODO) {
                        editEditDate.visibility = View.VISIBLE
                        editEditDay.visibility = View.VISIBLE
                        slash3.visibility = View.VISIBLE
                        editEditMonth.visibility = View.VISIBLE
                        slash4.visibility = View.VISIBLE
                        editEditYear.visibility = View.VISIBLE
                        editEditTime.visibility = View.VISIBLE
                        editEditHr.visibility = View.VISIBLE
                        colon2.visibility = View.VISIBLE
                        editEditMin.visibility = View.VISIBLE
                 }
                    else if(note.getType() == Type.IDEA) {
                        editRadioGroup.visibility = View.VISIBLE
                   }
                    open = false
                }else{
                    if(note.getType() == Type.TODO) {

                        val c = CustomDialog(requireContext())
                        val r = c.show("confirm", "Are u sure u want to change the alarm?")
                        if(r == ResponseType.YES){
                            val day = editEditDay.text.toString()
                            val month = editEditMonth.text.toString()
                            val year = editEditYear.text.toString()
                            val hour = editEditHr.text.toString()
                            val minute = editEditMin.text.toString()

                            if (day == "" || month == "" || year == "" || hour == "" || minute == "") {
                                Toast.makeText(context, "Date and Time have not been inputed", Toast.LENGTH_SHORT).show()
                            } else {
                                val selectedDay = day.toInt()
                                val selectedMonth = month.toInt()
                                val selectedYear = year.toInt()
                                val selectedHour = hour.toInt()
                                val selectedMin = minute.toInt()
                                val monthName = when (selectedMonth) {
                                    1 -> Calendar.JANUARY
                                    2 -> Calendar.FEBRUARY
                                    3 -> Calendar.MARCH
                                    4 -> Calendar.APRIL
                                    5 -> Calendar.MAY
                                    6 -> Calendar.JUNE
                                    7 -> Calendar.JULY
                                    8 -> Calendar.AUGUST
                                    9 -> Calendar.SEPTEMBER
                                    10 -> Calendar.OCTOBER
                                    11 -> Calendar.NOVEMBER
                                    12 -> Calendar.DECEMBER
                                    else -> null

                                }


                                val calendar = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, selectedYear)
                                    set(Calendar.MONTH, monthName!!)
                                    set(Calendar.DAY_OF_MONTH, selectedDay)
                                    set(Calendar.HOUR_OF_DAY, selectedHour)
                                    set(Calendar.MINUTE, selectedMin)
                                }

                                val time = "D-$selectedDay/$selectedMonth/$selectedYear  T-$selectedHour:$selectedMin"
                                resetAlarm(calendar.timeInMillis, note.getId(), time)
                            }
                        }

                        editEditDate.visibility = View.GONE
                        editEditDay.visibility = View.GONE
                        slash3.visibility = View.GONE
                        editEditMonth.visibility = View.GONE
                        slash4.visibility = View.GONE
                        editEditYear.visibility = View.GONE
                        editEditTime.visibility = View.GONE
                        editEditHr.visibility = View.GONE
                        colon2.visibility = View.GONE
                        editEditMin.visibility = View.GONE
                    }
                    else if(note.getType() == Type.IDEA) {

                        val c = CustomDialog(requireContext())
                        val r = c.show("confirm", "Are u sure u want to change the alarm?")
                        if(r == ResponseType.YES){
                            //cancel alarm
                            if (note.getAlarm()?.substring(0, 5) == "daily") {
                                cancelAlarm(note.getId())
                            } else {
                                val id = IdFinder()
                                val shelby = id.getId("${note.getAlarm()}")
                                for (i in shelby) {
                                    cancelAlarm(i)
                                }
                            }

                            //redo alarm
                            if (editRadioButtonAlarmDaily.isChecked) {
                                note.setId(++LUKAKU)
                                note.setAlarm("daily  $id")
                                setIdeaDayAlarm(note.getId(), note.getAlarm())
                            }
                            if (editRadioButtonAlarmWeekly.isChecked) {

                                var a: Int = 0
                                var b: Int = 0
                                var c: Int = 0
                                var d: Int = 0
                                var e: Int = 0
                                var f: Int = 0
                                var g: Int = 0

                                var collection: ArrayList<Collect> = ArrayList()

                                if (checkBoxSun2.isChecked) {
                                    a = ++LUKAKU
                                    collection.add(Collect(a, Calendar.SUNDAY))
                                }
                                if (checkBoxMon2.isChecked) {
                                    b = ++LUKAKU
                                    collection.add(Collect(b, Calendar.MONDAY))
                                }
                                if (checkBoxTue2.isChecked) {
                                    c = ++LUKAKU
                                    collection.add(Collect(c, Calendar.TUESDAY))
                                }
                                if (checkBoxWed2.isChecked) {
                                    d = ++LUKAKU
                                    collection.add(Collect(d, Calendar.WEDNESDAY))
                                }
                                if (checkBoxThur2.isChecked) {
                                    e = ++LUKAKU
                                    collection.add(Collect(e, Calendar.THURSDAY))
                                }
                                if (checkBoxFri2.isChecked) {
                                    f = ++LUKAKU
                                    collection.add(Collect(f, Calendar.FRIDAY))
                                }
                                if (checkBoxSat2.isChecked) {
                                    g = ++LUKAKU
                                    collection.add(Collect(g, Calendar.SATURDAY))
                                }

                                note.setAlarm("weekly $a,$b,$c,$d,$e,$f,$g")
                                collection.forEach {
                                    val calendar = Calendar.getInstance().apply {
                                        set(Calendar.DAY_OF_WEEK, it.day)
                                        set(Calendar.HOUR_OF_DAY, 12)
                                        set(Calendar.MINUTE, 0)
                                    }
                                    setIdeaWeekAlarm(calendar.timeInMillis, it.no, note.getAlarm())
                                }

                            }
                        }

                        editRadioGroup.visibility = View.GONE
                        editViewWeekly.visibility = View.GONE
                        checkBoxSun2.visibility = View.GONE
                        checkBoxMon2.visibility = View.GONE
                        checkBoxTue2.visibility = View.GONE
                        checkBoxWed2.visibility = View.GONE
                        checkBoxThur2.visibility = View.GONE
                        checkBoxFri2.visibility = View.GONE
                        checkBoxSat2.visibility = View.GONE
                    }
                    open = true
                }

            }

            editRadioGroup.setOnCheckedChangeListener { _, _ ->

                editViewWeekly.visibility = View.GONE
                checkBoxMon2.visibility = View.GONE
                checkBoxTue2.visibility = View.GONE
                checkBoxWed2.visibility = View.GONE
                checkBoxThur2.visibility = View.GONE
                checkBoxFri2.visibility = View.GONE
                checkBoxSat2.visibility = View.GONE
                checkBoxSun2.visibility = View.GONE

                if(editRadioButtonAlarmWeekly.isChecked){
                    editViewWeekly.visibility = View.VISIBLE
                    checkBoxSun2.visibility = View.VISIBLE
                    checkBoxMon2.visibility = View.VISIBLE
                    checkBoxTue2.visibility = View.VISIBLE
                    checkBoxWed2.visibility = View.VISIBLE
                    checkBoxThur2.visibility = View.VISIBLE
                    checkBoxFri2.visibility = View.VISIBLE
                    checkBoxSat2.visibility = View.VISIBLE
                }

            }

        } }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun resetAlarm(timeInMilliSeconds: Long, id: Int, alarm: String) {

        if (timeInMilliSeconds > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)

            intent.putExtra("noteType", "To do")
            intent.putExtra("alarm", alarm)
            intent.putExtra("timestamp", timeInMilliSeconds)
            intent.putExtra("noteTitle", binding.editEditTitle.text.toString())

            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(context, "Alarm is reset", Toast.LENGTH_SHORT).show()

        }

    }

    private fun cancelAlarm(id: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    private fun setIdeaDayAlarm(id: Int, alarm: String) {

        val calendar = System.currentTimeMillis()

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context?.applicationContext, AlarmReceiver::class.java)

        intent.putExtra("noteType", "Idea")
        intent.putExtra("timestamp", calendar)
        intent.putExtra("noteTitle", binding.editEditTitle.text.toString())
        intent.putExtra("alarm", alarm)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar,
            24 * 60 * 60 * 1000,
            pendingIntent
        )
        Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()

    }

    private fun setIdeaWeekAlarm(timeInMilliSeconds: Long, id: Int, alarm: String) {

        if (timeInMilliSeconds > 0) {

            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context?.applicationContext, AlarmReceiver::class.java)

            intent.putExtra("noteType", "Idea")
            intent.putExtra("timestamp", timeInMilliSeconds)
            intent.putExtra("noteTitle", binding.editEditTitle.text.toString())
            intent.putExtra("alarm", alarm)

            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                timeInMilliSeconds,
                7 * 24 * 60 * 60 * 1000,
                pendingIntent
            )
            Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()

        }

    }

}