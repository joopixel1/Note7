package com.pixie.note7.weekdata

class IdFinder {

    fun getId(alarm: String): ArrayList<Int>{

        var id: ArrayList<Int> = ArrayList()

        val alpha = alarm.split(" ")
        val number = alpha[1]

        val days = number.split(",")

        val a = days[0]
        if(a != "0"){
            id.add(("9$a").toInt())
        }

        val b = days[1]
        if(b != "0"){
            id.add(("9$b").toInt())
        }

        val c = days[2]
        if(c != "0"){
            id.add(("9$c").toInt())
        }

        val d = days[3]
        if(d != "0"){
            id.add(("9$d").toInt())
        }

        val e = days[4]
        if(e != "0"){
            id.add(("9$e").toInt())
        }

        val f = days[5]
        if(f != "0"){
            id.add(("9$f").toInt())
        }

        val g = days[6]
        if(g != "0"){
            id.add(("9$g").toInt())
        }

        return id

    }
}