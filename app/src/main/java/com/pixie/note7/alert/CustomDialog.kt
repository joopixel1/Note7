package com.pixie.note7.alert

import android.app.AlertDialog
import android.content.Context

class CustomDialog(context: Context) : AlertDialog.Builder(context) {

    fun show(title: String, message: String): ResponseType {
        var resp = ResponseType.NO
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        // performing positive action
        builder.setPositiveButton("Yes") { _, _ ->
            resp = ResponseType.YES
        }

        // performing negative action
        builder.setNegativeButton("No") { _, _ ->
            resp = ResponseType.NO
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        return resp
    }

}