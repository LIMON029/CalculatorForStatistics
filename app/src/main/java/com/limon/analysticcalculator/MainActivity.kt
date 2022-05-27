package com.limon.analysticcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.limon.analysticcalculator.databinding.ActivityMainBinding
import com.limon.analysticcalculator.fragment.TFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private val tFragment: TFragment = TFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
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