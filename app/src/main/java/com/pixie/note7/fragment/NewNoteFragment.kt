package com.pixie.note7.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.api.Note
import com.pixie.note7.api.Note.Companion.LUKAKU
import com.pixie.note7.api.Type
import com.pixie.note7.databinding.FragmentNewNoteBinding
import com.pixie.note7.receiver.AlarmReceiver
import com.pixie.note7.weekdata.Collect
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NewNoteFragment : Fragment() {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            radioGroup1.clearCheck()
            radioGroup1.setOnCheckedChangeListener { _, _ ->
                textNewDate.visibility = View.GONE
                textNewTime.visibility = View.GONE
                editNewDay.visibility = View.GONE
                editNewMonth.visibility = View.GONE
                editNewYear.visibility = View.GONE
                editNewHr.visibility = View.GONE
                editNewMin.visibility = View.GONE
                colon1.visibility = View.GONE
                slash1.visibility = View.GONE
                slash2.visibility = View.GONE


                textAlarmIdea.visibility = View.GONE
                checkBoxAlarmIdea.visibility = View.GONE
                radioGroup2.visibility = View.GONE
                textViewWeekly.visibility = View.GONE
                checkBoxMon.visibility = View.GONE
                checkBoxTue.visibility = View.GONE
                checkBoxWed.visibility = View.GONE
                checkBoxThur.visibility = View.GONE
                checkBoxFri.visibility = View.GONE
                checkBoxSat.visibility = View.GONE
                checkBoxSun.visibility = View.GONE


                if(radioButtonIdea.isChecked){
                    Log.d("check", "I just clicked idea")
                    textAlarmIdea.visibility = View.VISIBLE
                    radioGroup2.visibility = View.VISIBLE
                    radioGroup2.clearCheck()
                }

                else if(radioButtonTodo.isChecked){
                    Log.d("check", "I just clicked todo")
                    textNewDate.visibility = View.VISIBLE
                    textNewTime.visibility = View.VISIBLE
                    editNewDay.visibility = View.VISIBLE
                    editNewMonth.visibility = View.VISIBLE
                    editNewYear.visibility = View.VISIBLE
                    editNewHr.visibility = View.VISIBLE
                    editNewMin.visibility = View.VISIBLE
                    colon1.visibility = View.VISIBLE
                    slash1.visibility = View.VISIBLE
                    slash2.visibility = View.VISIBLE
                }

            }

            radioGroup2.setOnCheckedChangeListener { _, _ ->

                textViewWeekly.visibility = View.GONE
                checkBoxMon.visibility = View.GONE
                checkBoxTue.visibility = View.GONE
                checkBoxWed.visibility = View.GONE
                checkBoxThur.visibility = View.GONE
                checkBoxFri.visibility = View.GONE
                checkBoxSat.visibility = View.GONE
                checkBoxSun.visibility = View.GONE

                if(radioButtonAlarmWeekly.isChecked){
                    textViewWeekly.visibility = View.VISIBLE
                    checkBoxSun.visibility = View.VISIBLE
                    checkBoxMon.visibility = View.VISIBLE
                    checkBoxTue.visibility = View.VISIBLE
                    checkBoxWed.visibility = View.VISIBLE
                    checkBoxThur.visibility = View.VISIBLE
                    checkBoxFri.visibility = View.VISIBLE
                    checkBoxSat.visibility = View.VISIBLE
                }

            }


            btnOK.setOnClickListener {
                if (!(radioGroup1.checkedRadioButtonId == -1
                            || editTitle.text.toString().trim() == ""
                            || editDescription.text.toString().trim() == "")) {

                    val tit = editTitle.text.toString()
                    val desc = editDescription.text.toString()
                    val radioButtonID = radioGroup1.checkedRadioButtonId
                    val radioButton = view.findViewById<View>(radioButtonID) as RadioButton
                    val typ = Type.values()[radioGroup1.indexOfChild(radioButton)]
                    var time = ""


                    if (radioButtonTodo.isChecked) {
                        val day = editNewDay.text.toString()
                        val month = editNewMonth.text.toString()
                        val year = editNewYear.text.toString()
                        val hour = editNewHr.text.toString()
                        val minute = editNewMin.text.toString()

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

                            time = "D-$selectedDay/$selectedMonth/$selectedYear  T-$selectedHour:$selectedMin"
                            setTodoAlarm(calendar.timeInMillis, LUKAKU + 1, time)
                        }
                    }

                    if (radioButtonIdea.isChecked) {

                        if (radioButtonAlarmDaily.isChecked) {
                            time = "daily  ${LUKAKU + 1}"
                            setIdeaDayAlarm(LUKAKU + 1, time)
                        }

                        if (radioButtonAlarmWeekly.isChecked) {

                            var a: Int = 0
                            var b: Int = 0
                            var c: Int = 0
                            var d: Int = 0
                            var e: Int = 0
                            var f: Int = 0
                            var g: Int = 0

                            var collection: ArrayList<Collect> = ArrayList()

                            if (checkBoxSun.isChecked) {
                                a = ++LUKAKU
                                collection.add(Collect(a, Calendar.SUNDAY))
                            }
                            if (checkBoxMon.isChecked) {
                                b = ++LUKAKU
                                collection.add(Collect(b, Calendar.MONDAY))
                            }
                            if (checkBoxTue.isChecked) {
                                c = ++LUKAKU
                                collection.add(Collect(c, Calendar.TUESDAY))
                            }
                            if (checkBoxWed.isChecked) {
                                d = ++LUKAKU
                                collection.add(Collect(d, Calendar.WEDNESDAY))
                            }
                            if (checkBoxThur.isChecked) {
                                e = ++LUKAKU
                                collection.add(Collect(e, Calendar.THURSDAY))
                            }
                            if (checkBoxFri.isChecked) {
                                f = ++LUKAKU
                                collection.add(Collect(f, Calendar.FRIDAY))
                            }
                            if (checkBoxSat.isChecked) {
                                g = ++LUKAKU
                                collection.add(Collect(g, Calendar.SATURDAY))
                            }

                            time = "weekly $a,$b,$c,$d,$e,$f,$g"
                            collection.forEach {
                                val calendar = Calendar.getInstance().apply {
                                    set(Calendar.DAY_OF_WEEK, it.day)
                                    set(Calendar.HOUR_OF_DAY, 12)
                                    set(Calendar.MINUTE, 0)
                                }
                                setIdeaWeekAlarm(calendar.timeInMillis, it.no, time)
                            }

                        }

                    }


                    val newNote = Note(tit, desc, typ, time)
                    (activity as MainActivity).createNewNote(newNote)
                    findNavController().navigate(R.id.action_NewNoteFragment_to_MainFragment)
                }
            }

            btnCancel.setOnClickListener {
                findNavController().navigate(R.id.action_NewNoteFragment_to_MainFragment)
            }
    } }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setTodoAlarm(timeInMilliSeconds: Long, id: Int, alarm: String) {

        if (timeInMilliSeconds > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val alarmManager = this.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireActivity().applicationContext, AlarmReceiver::class.java)

            intent.putExtra("noteType", "To do")
            intent.putExtra("alarm", alarm)
            intent.putExtra("timestamp", timeInMilliSeconds)
            intent.putExtra("noteTitle", binding.editTitle.text.toString())

            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()

        }

    }

    private fun setIdeaDayAlarm(id: Int, alarm: String) {

        val calendar = System.currentTimeMillis()

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context?.applicationContext, AlarmReceiver::class.java)

        intent.putExtra("noteType", "Idea")
        intent.putExtra("timestamp", calendar)
        intent.putExtra("noteTitle", binding.editTitle.text.toString())
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
            intent.putExtra("noteTitle", binding.editTitle.text.toString())
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