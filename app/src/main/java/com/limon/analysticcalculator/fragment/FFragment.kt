package com.limon.analysticcalculator.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isGone
import com.limon.analysticcalculator.R
import com.limon.analysticcalculator.databinding.FragmentFBinding
import com.limon.analysticcalculator.utils.*
import java.lang.Exception

class FFragment : Fragment() {
    private var _binding: FragmentFBinding ?= null
    private val binding get() = _binding!!

    private val alphaList = getFAlphaList()
    private lateinit var dataPref: SharedPreferences

    private lateinit var fDatas: List<DataTypes.FData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFBinding.inflate(inflater, container, false)
        dataPref = activity?.getSharedPreferences(CONST.Data_DB, Context.MODE_PRIVATE)
            ?: throw Exception("No Pref")
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            fMainWrapper.setOnTouchListener { _, _ ->
                fAlphaInput.clearFocus()
                fDf1Input.clearFocus()
                fDf2Input.clearFocus()
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            }
        }
        initEditText()
        initSubmitBtn()
        initResetBtn()
    }

    private fun initEditText() = with(binding) {
        fAlphaInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                fAlphaInput.clearFocus()
                true
            }
            else false
        }
        fAlphaInput.setOnFocusChangeListener { view, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        fDf1Input.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                fDf1Input.clearFocus()
                true
            }
            else false
        }
        fDf1Input.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        fDf2Input.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                fDf1Input.clearFocus()
                true
            }
            else false
        }
        fDf2Input.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun initSubmitBtn() = with(binding) {
        val dfRange = getDFRange()
        fSubmitBtn.setOnClickListener {
            fAlphaInput.clearFocus()
            fDf1Input.clearFocus()
            fDf2Input.clearFocus()
            val alpha = fAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
            val df1 = fDf1Input.text.toString().toIntOrNull() ?: -1
            val df2 = fDf2Input.text.toString().toIntOrNull() ?: -1
            if(!alphaList.contains(alpha)){
                Toast.makeText(context, "잘못된 alpha값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(!dfRange.contains(df1)){
                Toast.makeText(context, "잘못된 자유도1입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(!dfRange.contains(df2)){
                Toast.makeText(context, "잘못된 자유도2입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fDatas = Parser.prefFDataParser(dataPref.getString("${CONST.FDATA}_${alpha}", "")?:"")
            val result = fDatas[101*(df2-1)+(df1-1)]
            fExplainText.text = "alpha값 ${alpha}에서 자유도1은 ${df1}, 자유도2는 ${df2}일 때의 F값"
            fResultText.text = result.f.toString()

            fAlphaInput.isEnabled = false
            fDf1Input.isEnabled = false
            fDf2Input.isEnabled = false
            fSubmitBtn.isGone = true
            fResetBtn.isGone = false
        }
    }

    private fun initResetBtn() = with(binding) {
        fResetBtn.setOnClickListener {
            fAlphaInput.isEnabled = true
            fDf1Input.isEnabled = true
            fDf2Input.isEnabled = true
            fAlphaInput.text.clear()
            fDf1Input.text.clear()
            fDf2Input.text.clear()
            fExplainText.text = ""
            fResultText.text = ""
            fSubmitBtn.isGone = false
            fResetBtn.isGone = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}