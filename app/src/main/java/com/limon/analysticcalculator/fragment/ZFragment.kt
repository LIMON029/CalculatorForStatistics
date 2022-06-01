package com.limon.analysticcalculator.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.limon.analysticcalculator.R
import com.limon.analysticcalculator.databinding.FragmentZBinding
import com.limon.analysticcalculator.utils.*
import java.lang.Exception

class ZFragment : Fragment() {
    private var _binding : FragmentZBinding ?= null
    private val binding get() = _binding!!

    private val oneNumList = getZAlphaList()

    private lateinit var zDatas: List<DataTypes.ZData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZBinding.inflate(inflater, container, false)
        val dataPref = activity?.getSharedPreferences(CONST.Data_DB, Context.MODE_PRIVATE)
            ?: throw Exception("No Pref")
        zDatas = Parser.prefZDataParser(dataPref.getString(CONST.ZDATA, "")?:"")
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            zMainWrapper.setOnTouchListener { _, _ ->
                zAlphaInput.clearFocus()
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            }
        }
        initEditText()
        initSubmitBtn()
        initResetBtn()
    }

    private fun initEditText() = with(binding) {
        zAlphaInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                zAlphaInput.clearFocus()
                true
            }
            else false
        }
        zAlphaInput.setOnFocusChangeListener { view, hasFocus ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initSubmitBtn() = with(binding) {
        zSubmitBtn.setOnClickListener {
            zAlphaInput.clearFocus()
            val alpha = zAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
            if(!oneNumList.contains(alpha)){
                Toast.makeText(context, "잘못된 z값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = zDatas[(alpha * 100).toInt()]
            zExplainText.text = "z=${alpha}에서의 값"
            zResultText.text = result.z.toString()

            zAlphaInput.isEnabled = false
            zSubmitBtn.isGone = true
            zResetBtn.isGone = false
        }
    }

    private fun initResetBtn() = with(binding) {
        zResetBtn.setOnClickListener {
            zAlphaInput.isEnabled = true
            zAlphaInput.text.clear()
            zExplainText.text = ""
            zResultText.text = ""
            zSubmitBtn.isGone = false
            zResetBtn.isGone = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TEST_ALL = "TEST_ALL"
        const val TEST_ONE = "TEST_ONE"
        const val ALL_GONE = "ALL_GONE"
        const val ALL_ACTIVE = "ALL_ACTIVE"
    }
}