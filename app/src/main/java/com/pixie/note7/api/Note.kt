package com.pixie.note7.api

import org.json.JSONException
import org.json.JSONObject



class Note {


    private val JSON_ID = "id"
    private val JSON_TITLE = "title"
    private val JSON_DESCRIPTION = "description"
    private val JSON_TYPE = "type"
    private val JSON_ALARM = "alarm"

    private var id: Int
    private var title: String
    private var description: String
    private val type: Type
    private var alarm: String

    constructor(tit: String, desc: String, typ: Type, alar: String) {
        this.id = LUKAKU
        this.title = tit
        this.description = desc
        this.type = typ
        this.alarm = alar
        LUKAKU++
    }

    @Throws(JSONException::class)
    constructor(jo: JSONObject) {
        this.id = jo.getInt(JSON_ID)
        this.title = jo.getString(JSON_TITLE)
        this.description = jo.getString(JSON_DESCRIPTION)
        this.type = Type.values()[jo.getInt(JSON_TYPE)]
        this.alarm = jo.getString(JSON_ALARM)
    }


    @Throws(JSONException::class)
    fun convertToJSON(): JSONObject {
        val jo = JSONObject()
        jo.put(JSON_ID, id)
        jo.put(JSON_TITLE, title)
        jo.put(JSON_DESCRIPTION, description)
        jo.put(JSON_TYPE, type.ordinal)
        jo.put(JSON_ALARM, alarm)
        return jo
    }

    fun getTitle(): String = title
    fun getDescription(): String = description
    fun getType(): Type = type
    fun getId(): Int = id
    fun getAlarm(): String = alarm
    fun setTitle(tit: String){ title = tit }
    fun setDescription(desc: String){ description = desc }
    fun setAlarm(alar: String){ alarm = alar }
    fun setId(i: Int) { id = i }

    companion object{
        var LUKAKU: Int = 0
    }

}