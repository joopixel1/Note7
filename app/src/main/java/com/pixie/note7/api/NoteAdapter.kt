package com.pixie.note7.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pixie.note7.fragment.MainFragment
import com.pixie.note7.R


class NoteAdapter(private val main: MainFragment, private val noteList: ArrayList<Note>): RecyclerView.Adapter<NoteAdapter.ListItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ListItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ListItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteAdapter.ListItemHolder, position: Int) {
        val note = noteList[position]
        holder.title.text = note.getTitle()
        val length1 = note.getDescription().length
        holder.description.text = if (length1 > 15) note.getDescription().substring(0, 15) else note.getDescription()
        holder.type.text = note.getType().name
        holder.alarm.text = if (note.getType() !=  Type.IDEA) note.getAlarm() else decodeForWeekly(note.getAlarm())
        if(note.getType() !=  Type.NOTE) {
            holder.image.visibility = View.VISIBLE
            holder.alarm.visibility = View.VISIBLE
        }


    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    private fun decodeForWeekly(word: String): String{

        if(word == ""){
            return ""
        }
        else if(word.substring(0, 5) == "daily"){
            return word.substring(0, 5)
        }
        else {
            var elijah = ""

            if (word.substring(7, 8) != "0") {
                elijah += " Sun"
            }
            if (word.substring(9, 10) != "0") {
                elijah += " Mon"
            }
            if (word.substring(11, 12) != "0") {
                elijah += " Tue"
            }
            if (word.substring(13, 14) != "0") {
                elijah += " Wed"
            }
            if (word.substring(15, 16) != "0") {
                elijah += " Thu"
            }
            if (word.substring(17, 18) != "0") {
                elijah += " Fri"
            }
            if (word.substring(19, 20) != "0") {
                elijah += " Sat"
            }
            return elijah
        }
    }


    inner class ListItemHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        internal val title = view.findViewById<View>(R.id.noteTitle) as TextView
        internal val description = view.findViewById<View>(R.id.noteDescription) as TextView
        internal val type = view.findViewById<View>(R.id.noteType) as TextView
        internal val image = view.findViewById<View>(R.id.noteImage) as ImageView
        internal val alarm = view.findViewById<View>(R.id.noteAlarm) as TextView

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }
        override fun onClick(view: View) {
            main.showNote(adapterPosition)
        }

    }

}