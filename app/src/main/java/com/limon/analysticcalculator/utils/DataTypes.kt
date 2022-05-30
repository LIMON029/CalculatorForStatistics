package com.limon.analysticcalculator.utils

class DataTypes {
    data class ZData(val alpha:Float, val z:Float)
    data class TData(val alpha:Float, val df:Int, val t:Float)
    data class ChiData(val alpha:Float, val df:Int, val chi:Float)
    data class FData(val alpha:Float, val df1:Int, val df2:Int, val f:Float)
}