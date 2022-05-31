package com.limon.analysticcalculator.utils

import com.limon.analysticcalculator.utils.DataTypes.*
import org.json.JSONArray

object Parser {
    private fun tDataParser(str: String): TData {
        val strList = str.replace(",", "")
            .replace(")", "")
            .replace("TData(alpha=", "")
            .replace("df=", "")
            .replace("t=", "")
            .split(" ")
        return TData(
            alpha = strList[0].toFloat(),
            df = strList[1].toInt(),
            t = strList[2].toFloat()
        )
    }

    private fun zDataParser(str: String): ZData {
        val strList = str.replace(",", "")
            .replace(")", "")
            .replace("ZData(alpha=", "")
            .replace("z=", "")
            .split(" ")
        return ZData(
            alpha = strList[0].toFloat(),
            z = strList[1].toFloat()
        )
    }

    private fun chiDataParser(str: String): ChiData {
        val strList = str.replace(",", "")
            .replace(")", "")
            .replace("ChiData(alpha=", "")
            .replace("df=", "")
            .replace("chi=", "")
            .split(" ")
        return ChiData(
            alpha = strList[0].toFloat(),
            df = strList[1].toInt(),
            chi = strList[2].toFloat()
        )
    }

    private fun FDataParser(str: String): FData {
        val strList = str.replace(",", "")
            .replace(")", "")
            .replace("FData(alpha=", "")
            .replace("df1=", "")
            .replace("df2=", "")
            .replace("f=", "")
            .split(" ")
        return FData(
            alpha = strList[0].toFloat(),
            df1 = strList[1].toInt(),
            df2 = strList[2].toInt(),
            f = strList[3].toFloat()
        )
    }

    fun prefTDataParser(saved: String): List<TData> {
        val myList = mutableListOf<TData>()
        val reJsonList = JSONArray(saved)
        for (i: Int in 0 until reJsonList.length()) {
            myList.add(tDataParser(reJsonList.optString(i)))
        }
        return myList
    }

    fun prefZDataParser(saved: String): List<ZData> {
        val myList = mutableListOf<ZData>()
        val reJsonList = JSONArray(saved)
        for (i: Int in 0 until reJsonList.length()) {
            myList.add(zDataParser(reJsonList.optString(i)))
        }
        return myList
    }

    fun prefChiDataParser(saved: String):List<ChiData> {
        val myList = mutableListOf<ChiData>()
        val reJsonList = JSONArray(saved)
        for (i: Int in 0 until reJsonList.length()) {
            myList.add(chiDataParser(reJsonList.optString(i)))
        }
        return myList
    }

    fun prefFDataParser(saved: String):List<FData> {
        val myList = mutableListOf<FData>()
        val reJsonList = JSONArray(saved)
        for (i: Int in 0 until reJsonList.length()) {
            myList.add(FDataParser(reJsonList.optString(i)))
        }
        return myList
    }
}