package com.limon.analysticcalculator.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
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
import com.limon.analysticcalculator.databinding.FragmentTBinding
import com.limon.analysticcalculator.utils.CONST.Data_DB
import com.limon.analysticcalculator.utils.CONST.TDATA
import com.limon.analysticcalculator.utils.DataTypes
import com.limon.analysticcalculator.utils.Parser
import com.limon.analysticcalculator.utils.getDFRange
import com.limon.analysticcalculator.utils.getTAlphaList
import java.lang.Exception

class TFragment : Fragment() {
    private var _binding: FragmentTBinding ?= null
    private val binding get() = _binding!!

    private val allNumList = listOf(0.1F, 0.05F, 0.01F, 0.005F, 0.001F)
    private val oneNumList = allNumList + listOf(0.4F, 0.25F, 0.025F, 0.0025F, 0.0005F)
    private var tTestAllCheck = false

    private lateinit var tDatas: List<DataTypes.TData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTBinding.inflate(inflater, container, false)
        val dataPref = activity?.getSharedPreferences(Data_DB, MODE_PRIVATE)
            ?: throw Exception("No Pref")
        tDatas = Parser.prefTDataParser(dataPref.getString(TDATA, "")?:"")
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tMainWrapper.setOnTouchListener { _, _ ->
                tAlphaInput.clearFocus()
                tDfInput.clearFocus()
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
        tAlphaInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                tAlphaInput.clearFocus()
                true
            }
            else false
        }
        tAlphaInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val alpha = tAlphaInput.text.toString().toFloatOrNull() ?: 0.0f
                if(allNumList.contains(alpha)){
                    setTTestGroupBtn(TEST_ALL)
                } else {
                    setTTestGroupBtn(TEST_ONE)
                }
                val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        tDfInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                tDfInput.clearFocus()
                true
            }
            else false
        }
        tDfInput.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRadioButtons() = with(binding) {
        tTestGroup.setOnCheckedChangeListener { _, i ->
            tTestAllCheck = i == R.id.t_test_all
        }

        tTestAll.setOnTouchListener { _, _ ->
            tAlphaInput.clearFocus()
            tDfInput.clearFocus()
            if(allNumList.contains(tAlphaInput.text.toString().toFloatOrNull()?:-1.0f)){
                setTTestGroupBtn(TEST_ALL)
                tTestAll.isSelected = true
            } else {
                setTTestGroupBtn(TEST_ONE)
            }
            false
        }
        tTestOne.setOnTouchListener { _, _ ->
            tAlphaInput.clearFocus()
            tDfInput.clearFocus()
            if(allNumList.contains(tAlphaInput.text.toString().toFloatOrNull()?:-1.0f)){
                setTTestGroupBtn(TEST_ALL)
            } else {
                setTTestGroupBtn(TEST_ONE)
            }
            tTestOne.isSelected = true
            false
        }
    }

    private fun initSubmitBtn() = with(binding) {
        val alphaList = getTAlphaList()
        val dfRange = getDFRange()
        tSubmitBtn.setOnClickListener {
            tAlphaInput.clearFocus()
            tDfInput.clearFocus()
            val alpha = tAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
            val df = tDfInput.text.toString().toIntOrNull() ?: -1
            if(!oneNumList.contains(alpha)){
                Toast.makeText(context, "잘못된 alpha값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(!dfRange.contains(df)){
                Toast.makeText(context, "잘못된 자유도입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(tTestGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "검정방법을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = tDatas[10*(df-1)+alphaList.indexOf(if(tTestAllCheck) alpha/2 else alpha)]
            tExplainText.text = "alpha값 ${alpha}에서 자유도 ${df}의 ${if(tTestAllCheck) "양측검정" else "단측검정"}일 때의 t값"
            tResultText.text = result.t.toString()

            tAlphaInput.isEnabled = false
            tDfInput.isEnabled = false
            tSubmitBtn.isGone = true
            tResetBtn.isGone = false
            setTTestGroupBtn(ALL_GONE)
        }
    }

    private fun initResetBtn() = with(binding) {
        tResetBtn.setOnClickListener {
            tAlphaInput.isEnabled = true
            tDfInput.isEnabled = true
            tAlphaInput.text.clear()
            tDfInput.text.clear()
            setTTestGroupBtn(ALL_ACTIVE)
            tExplainText.text = ""
            tResultText.text = ""
            tSubmitBtn.isGone = false
            tResetBtn.isGone = true
        }
    }

    private fun setTTestGroupBtn(case:String) = with(binding) {
        when(case) {
            TEST_ALL -> {
                tTestAll.isClickable = true
                tTestGroup.clearCheck()
                context?.let {
                    tTestAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                }
            }
            TEST_ONE -> {
                tTestAll.isClickable = false
                tTestGroup.clearCheck()
                context?.let {
                    tTestAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_GONE -> {
                tTestAll.isEnabled = false
                tTestOne.isEnabled = false
                context?.let {
                    tTestAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                    tTestOne.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_ACTIVE -> {
                tTestAll.isEnabled = true
                tTestOne.isEnabled = true
                tTestGroup.clearCheck()
                context?.let {
                    tTestAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                    tTestOne.setTextColor(ContextCompat.getColor(it, R.color.white))
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