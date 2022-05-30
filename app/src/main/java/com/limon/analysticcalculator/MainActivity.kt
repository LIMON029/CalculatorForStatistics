package com.limon.analysticcalculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.limon.analysticcalculator.utils.getData
import com.limon.analysticcalculator.databinding.ActivityMainBinding
import com.limon.analysticcalculator.fragment.ChiFragment
import com.limon.analysticcalculator.fragment.FFragment
import com.limon.analysticcalculator.fragment.TFragment
import com.limon.analysticcalculator.fragment.ZFragment
import com.limon.analysticcalculator.utils.CONST.CHIDATA
import com.limon.analysticcalculator.utils.CONST.Data_DB
import com.limon.analysticcalculator.utils.CONST.FDATA
import com.limon.analysticcalculator.utils.CONST.TDATA
import com.limon.analysticcalculator.utils.CONST.ZDATA
import com.limon.analysticcalculator.utils.getFAlphaList
import com.limon.analysticcalculator.utils.getFData
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private lateinit var dataPreferences:SharedPreferences

    private val zFragment: ZFragment = ZFragment()
    private val tFragment: TFragment = TFragment()
    private val chiFragment: ChiFragment = ChiFragment()
    private val fFragment: FFragment = FFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataPreferences = getSharedPreferences(Data_DB, MODE_PRIVATE)

        initDatas()
//        initBtn()
    }

    private fun initDatas() {
        if(dataPreferences.getString(ZDATA, null) != null){
            Log.e("MMAIN", "NULL")
            return
        }
        val editor = dataPreferences.edit()
        val zDatas = getData(context = baseContext, ZDATA)
        val tDatas = getData(context = baseContext, TDATA)
        val chiData = getData(context = baseContext, CHIDATA)
        val fData = getFData(context = baseContext)
        val fAlphas = getFAlphaList()
        val datas = mutableMapOf(ZDATA to zDatas, TDATA to tDatas, CHIDATA to chiData)
        for(i in fData.indices) {
            datas["${FDATA}_${fAlphas[i]}"] = fData[i]
        }
        for(key in datas.keys){
            val jsonList = JSONArray()
            for(data in datas[key]!!){
                jsonList.put(data)
            }
            Log.e("MMAIN", jsonList.toString())
            editor.putString(key, jsonList.toString())
            editor.apply()
        }
    }

    private fun initBtn() = with(binding) {
        zBtn.setOnClickListener { replaceFragment(Z) }
        tBtn.setOnClickListener { replaceFragment(T) }
        chiBtn.setOnClickListener { replaceFragment(CHI) }
        fBtn.setOnClickListener { replaceFragment(F) }
    }

    private fun replaceFragment(type:Int) {
        lateinit var fragment: Fragment
        when(type) {
            Z -> fragment = zFragment
            T -> fragment = tFragment
            CHI -> fragment = chiFragment
            F -> fragment = fFragment
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .show(fragment)
            .commitNowAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val Z = 0
        private const val T = 1
        private const val CHI = 2
        private const val F = 3
    }
}