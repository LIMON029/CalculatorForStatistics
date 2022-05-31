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
    private var zTestAllCheck = false

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
        initRadioButtons()
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
            if(!hasFocus) {
                val alpha = zAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
                if(oneNumList.contains(alpha / 2)) {
                    setZTestGroupBtn(TEST_ALL)
                } else {
                    setZTestGroupBtn(TEST_ONE)
                }
            }
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRadioButtons() = with(binding) {
        zTestGroup.setOnCheckedChangeListener { _, i ->
            zTestAllCheck = i == R.id.t_test_all
        }

        zTestAll.setOnTouchListener { _, _ ->
            zAlphaInput.clearFocus()
            val alpha = zAlphaInput.text.toString().toFloat()
            if(oneNumList.contains(alpha / 2)){
                setZTestGroupBtn(TFragment.TEST_ALL)
                zTestAll.isSelected = true
            } else {
                setZTestGroupBtn(TFragment.TEST_ONE)
            }
            false
        }
        zTestOne.setOnTouchListener { _, _ ->
            zAlphaInput.clearFocus()
            val alpha = zAlphaInput.text.toString().toFloat()
            if(oneNumList.contains(alpha / 2)){
                setZTestGroupBtn(TFragment.TEST_ALL)
            } else {
                setZTestGroupBtn(TFragment.TEST_ONE)
            }
            zTestOne.isSelected = true
            false
        }
    }

    private fun initSubmitBtn() = with(binding) {
        zSubmitBtn.setOnClickListener {
            zAlphaInput.clearFocus()
            val alpha = zAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
            if(!oneNumList.contains(alpha)){
                Toast.makeText(context, "잘못된 alpha값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(zTestGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "검정방법을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = zDatas[(alpha * 100).toInt()]
            zExplainText.text = "alpha값 ${alpha}에서 ${if(zTestAllCheck) "양측검정" else "단측검정"}일 때의 z값"
            zResultText.text = result.z.toString()

            zAlphaInput.isEnabled = false
            zSubmitBtn.isGone = true
            zResetBtn.isGone = false
            setZTestGroupBtn(TFragment.ALL_GONE)
        }
    }

    private fun initResetBtn() = with(binding) {
        zResetBtn.setOnClickListener {
            zAlphaInput.isEnabled = true
            zAlphaInput.text.clear()
            setZTestGroupBtn(TFragment.ALL_ACTIVE)
            zExplainText.text = ""
            zResultText.text = ""
            zSubmitBtn.isGone = false
            zResetBtn.isGone = true
        }
    }

    private fun setZTestGroupBtn(case:String) = with(binding) {
        when(case) {
            TEST_ALL -> {
                zTestAll.isClickable = true
                zTestGroup.clearCheck()
                context?.let {
                    zTestAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                }
            }
            TEST_ONE -> {
                zTestAll.isClickable = false
                zTestGroup.clearCheck()
                context?.let {
                    zTestAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_GONE -> {
                zTestAll.isEnabled = false
                zTestOne.isEnabled = false
                context?.let {
                    zTestAll.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                    zTestOne.setTextColor(ContextCompat.getColor(it, R.color.hint_color))
                }
            }
            ALL_ACTIVE -> {
                zTestAll.isEnabled = true
                zTestOne.isEnabled = true
                zTestGroup.clearCheck()
                context?.let {
                    zTestAll.setTextColor(ContextCompat.getColor(it, R.color.white))
                    zTestOne.setTextColor(ContextCompat.getColor(it, R.color.white))
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