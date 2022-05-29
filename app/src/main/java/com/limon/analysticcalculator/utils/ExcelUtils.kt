package com.limon.analysticcalculator.utils

import android.content.Context
import jxl.Workbook
import com.limon.analysticcalculator.utils.DataTypes.*

private val alphaList = listOf(0.4f, 0.25f, 0.1f, 0.05f, 0.025f, 0.01f, 0.005f, 0.0025f, 0.001f, 0.0005f)

fun getAlphaList():List<Float> {
    return alphaList
}

fun getTData(context:Context):List<TData> {
    val tDatas = mutableListOf<TData>()
    try {
        val input = context.assets.open("tdata.xls")
        val wb = Workbook.getWorkbook(input)
        if(wb != null) {
            val sheet = wb.getSheet(0)
            if(sheet != null) {
                val colTotal = sheet.columns
                val rowTotal = sheet.getColumn(colTotal - 1).size

                for(row:Int in 0 until rowTotal) {
                    for(col:Int in 0 until colTotal) {
                        val contents = sheet.getCell(col, row).contents
                        tDatas.add(TData(
                            alpha = alphaList[col],
                            df = if (row != rowTotal-1) row + 1 else 120,
                            t = contents.toFloat()
                        ))
                    }

                }
            }
        }
    } catch (e:Exception) {
        e.printStackTrace()
    }
    return tDatas
}