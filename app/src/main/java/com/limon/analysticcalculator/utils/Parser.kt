package com.limon.analysticcalculator.utils

import com.limon.analysticcalculator.utils.DataTypes.*
import org.json.JSONArray

object Parser {
    private fun tDataParser(str:String): TData {
        val strList = str.replace(",", "")
            .replace(")", "")
            .replace("TData(alpha=", "")
            .replace("df=", "")
            .replace("t=", "")
            .split(" ")
        val data = TData(
            alpha = strList[0].toFloat(),
            df = strList[1].toInt(),
            t = strList[2].toFloat()
        )
        return data

    }
    fun prefTDataParser(saved:String):List<TData> {
        val myList = mutableListOf<TData>()
        val reJsonList = JSONArray(saved)
        for(i:Int in 0 until reJsonList.length()) {
            myList.add(tDataParser(reJsonList.optString(i)))
        }
        return myList
    }
}