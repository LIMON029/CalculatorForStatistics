package com.limon.analysticcalculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.limon.analysticcalculator.utils.CONST.APPINFO_DB
import com.limon.analysticcalculator.utils.getTData
import com.limon.analysticcalculator.databinding.ActivityMainBinding
import com.limon.analysticcalculator.fragment.TFragment
import com.limon.analysticcalculator.utils.CONST.Data_DB
import com.limon.analysticcalculator.utils.CONST.TDATA
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private lateinit var appInfoPreferences: SharedPreferences
    private lateinit var dataPreferences:SharedPreferences

    private val tFragment: TFragment = TFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appInfoPreferences = getSharedPreferences(APPINFO_DB, MODE_PRIVATE)
        dataPreferences = getSharedPreferences(Data_DB, MODE_PRIVATE)


        initDatas()
        initViews()
    }

    fun initDatas() {
        val editor = dataPreferences.edit()
        val jsonList = JSONArray()
        val tDatas = getTData(context = baseContext)
        for(i in tDatas){
            jsonList.put(i)
        }
        editor.putString(TDATA, jsonList.toString())
        editor.apply()
    }

    fun initViews() = with(binding) {
        tBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, tFragment)
                .show(tFragment)
                .commitNowAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}