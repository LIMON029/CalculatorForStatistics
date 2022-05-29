package com.limon.analysticcalculator.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import com.limon.analysticcalculator.R
import com.limon.analysticcalculator.databinding.FragmentTBinding
import com.limon.analysticcalculator.utils.CONST.Data_DB
import com.limon.analysticcalculator.utils.CONST.TDATA
import com.limon.analysticcalculator.utils.DataTypes
import com.limon.analysticcalculator.utils.Parser
import com.limon.analysticcalculator.utils.getAlphaList
import java.lang.Exception

class TFragment : Fragment() {
    private var _binding: FragmentTBinding ?= null
    private val binding get() = _binding!!

    private val allNumList = listOf(0.1F, 0.05F, 0.01F, 0.005F, 0.001F)
    private val oneNumList = allNumList + listOf(0.4F, 0.25F, 0.025F, 0.0025F, 0.0005F)
    private var testAllCheck = false

    private lateinit var TDatas: List<DataTypes.TData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTBinding.inflate(inflater, container, false)
        val dataPref = activity?.getSharedPreferences(Data_DB, MODE_PRIVATE)
            ?: throw Exception("No Pref")
        TDatas = Parser.prefTDataParser(dataPref.getString(TDATA, "")?:"")
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            mainWrapper.setOnTouchListener { view, motionEvent ->
                alphaInput.clearFocus()
                dfInput.clearFocus()
                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            }
        }
        initEditText()
        initRadioButtons()
        initSubmitBtn()
        initResetBtn()
    }

    private fun initEditText() = with(binding) {
        alphaInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                alphaInput.clearFocus()
                true
            }
            else false
        }
        alphaInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val alpha = alphaInput.text.toString().toFloatOrNull() ?: 0.0f
                if(allNumList.contains(alpha)){
                    setTestGroupBtn(TEST_ALL)
                } else {
                    setTestGroupBtn(TEST_ONE)
                }
                val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        dfInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                dfInput.clearFocus()
                true
            }
            else false
        }
        dfInput.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRadioButtons() = with(binding) {
        testGroup.setOnCheckedChangeListener { _, i ->
            testAllCheck = i == R.id.test_all
        }

        testAll.setOnTouchListener { _, _ ->
            alphaInput.clearFocus()
            dfInput.clearFocus()
            if(allNumList.contains(alphaInput.text.toString().toFloat())){
                setTestGroupBtn(TEST_ALL)
                testAll.isSelected = true
            } else {
                setTestGroupBtn(TEST_ONE)
            }
            false
        }
        testOne.setOnTouchListener { _, _ ->
            alphaInput.clearFocus()
            dfInput.clearFocus()
            if(allNumList.contains(alphaInput.text.toString().toFloat())){
                setTestGroupBtn(TEST_ALL)
            } else {
                setTestGroupBtn(TEST_ONE)
                testOne.isSelected = true
            }
            false
        }
    }

    private fun initSubmitBtn() = with(binding) {
        val alphaList = getAlphaList()
        submitBtn.setOnClickListener {
            alphaInput.clearFocus()
            dfInput.clearFocus()
            val alpha = alphaInput.text.toString().toFloatOrNull() ?: -1.0f
            val df = dfInput.text.toString().toIntOrNull() ?: -1
            if(!oneNumList.contains(alpha)){
                Toast.makeText(context, "잘못된 alpha값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(df < 0){
                Toast.makeText(context, "잘못된 자유도입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(testGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "검정방법을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = TDatas[10*(df-1)+alphaList.indexOf(if(testAllCheck) alpha/2 else alpha)]
            explainText.text = "alpha값 ${alpha}에서 자유도 ${df}의 ${if(testAllCheck) "양측검정" else "단측검정"}일 때의 t값"
            resultText.text = result.t.toString()

            alphaInput.isClickable = false
            dfInput.isClickable = false
            submitBtn.isGone = true
            resetBtn.isGone = false
            setTestGroupBtn(ALL_GONE)
        }
    }

    private fun initResetBtn() = with(binding) {
        resetBtn.setOnClickListener {
            alphaInput.isClickable = true
            dfInput.isClickable = true
            alphaInput.text.clear()
            dfInput.text.clear()
            setTestGroupBtn(ALL_ACTIVE)
            explainText.text = ""
            resultText.text = ""
            submitBtn.isGone = false
            resetBtn.isGone = true
        }
    }

    private fun setTestGroupBtn(case:String) = with(binding) {
        when(case) {
            TEST_ALL -> {
                testAll.isClickable = true
                testGroup.clearCheck()
                context?.let {
                    testAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                }
            }
            TEST_ONE -> {
                testAll.isClickable = false
                testGroup.clearCheck()
                context?.let {
                    testAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_GONE -> {
                testAll.isClickable = false
                testOne.isClickable = false
                context?.let {
                    testAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                    testOne.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_ACTIVE -> {
                testAll.isClickable = true
                testOne.isClickable = true
                testGroup.clearCheck()
                context?.let {
                    testAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                    testOne.setTextColor(ContextCompat.getColor(it, R.color.white))
                }
            }
            else -> {}
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