package com.limon.analysticcalculator.utils

import android.content.Context
import com.limon.analysticcalculator.utils.CONST.CHIDATA
import com.limon.analysticcalculator.utils.CONST.FDATA
import com.limon.analysticcalculator.utils.CONST.TDATA
import com.limon.analysticcalculator.utils.CONST.ZDATA
import jxl.Workbook
import com.limon.analysticcalculator.utils.DataTypes.*

private val zAlphaList = mutableListOf<Float>()
private val tAlphaList = listOf(0.4f, 0.25f, 0.1f, 0.05f, 0.025f, 0.01f, 0.005f, 0.0025f, 0.001f, 0.0005f)
private val chiAlphaList = listOf(0.995f, 0.99f, 0.975f, 0.95f, 0.9f, 0.1f, 0.05f, 0.025f, 0.01f, 0.005f)
private val fAlphaList = listOf(0.1f, 0.05f, 0.005f, 0.01f, 0.025f)

private val fileNameList = mapOf(
    ZDATA to "zdata.xls",
    TDATA to "tdata.xls",
    CHIDATA to "chidata.xls"
)

private fun initZAlphaList() {
    var num = 0.00
    zAlphaList.add(num.toFloat())
    for(i:Int in 1..399) {
        num += 0.01
        zAlphaList.add(num.toFloat())
    }
}

fun getTAlphaList():List<Float> {
    return tAlphaList
}

fun getZAlphaList():List<Float> {
    initZAlphaList()
    return zAlphaList
}

fun getChiAlphaList():List<Float> {
    return chiAlphaList
}

fun getFAlphaList():List<Float> {
    return fAlphaList
}

fun getDFRange(): List<Int> {
    return (1..100).toList() + listOf(120)
}

fun getData(context:Context, dataType:String):List<Any> {
    initZAlphaList()
    val datas = mutableListOf<Any>()
    try {
        val input = context.assets.open(fileNameList[dataType]?:"")
        val wb = Workbook.getWorkbook(input)
        if (wb != null) {
            val sheet = wb.getSheet(0)
            if (sheet != null) {
                val colTotal = sheet.columns
                val rowTotal = sheet.getColumn(colTotal - 2).size
                for (row: Int in 0 until rowTotal-1) {
                    for (col: Int in 0 until colTotal-1) {
                        val contents = sheet.getCell(col, row).contents
                        when (dataType) {
                            ZDATA -> {
                                datas.add(
                                    ZData(
                                        alpha = zAlphaList[col],
                                        z = contents.toFloat()
                                    )
                                )
                            }
                            TDATA -> {
                                datas.add(
                                    TData(
                                        alpha = tAlphaList[col],
                                        df = if (row != rowTotal - 1) row + 1 else 120,
                                        t = contents.toFloat()
                                    )
                                )
                            }
                            CHIDATA -> {
                                datas.add(
                                    ChiData(
                                        alpha = chiAlphaList[col],
                                        df = if (row != rowTotal - 1) row + 1 else 120,
                                        chi = contents.toFloat()
                                    )
                                )
                            }
                        }
                    }

                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return datas
}

fun getFData(context: Context): List<List<Any>>{
    val fData = mutableListOf<List<Any>>()
    val fAlphaList = listOf(0.1f, 0.05f, 0.005f, 0.01f, 0.025f)
    for(i in fAlphaList) {
        val datas = mutableListOf<Any>()
        try {
            val input = context.assets.open("fdata${i.toString().replace(".","")}.xls")
            val wb = Workbook.getWorkbook(input)
            if (wb != null) {
                val sheet = wb.getSheet(0)
                if (sheet != null) {
                    val colTotal = sheet.columns
                    val rowTotal = sheet.getColumn(colTotal - 1).size
                    for (row: Int in 0 until rowTotal) {
                        for (col: Int in 0 until colTotal) {
                            val contents = sheet.getCell(col, row).contents
                            datas.add(
                                FData(
                                    alpha = i,
                                    df1 = if (col != rowTotal - 1) row + 1 else 120,
                                    df2 = if (row != rowTotal - 1) row + 1 else 120,
                                    f = contents.toFloat()
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fData.add(datas)
    }
    return fData
}