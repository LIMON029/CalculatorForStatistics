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
import androidx.core.view.isGone
import com.limon.analysticcalculator.databinding.FragmentChiBinding
import com.limon.analysticcalculator.utils.*
import java.lang.Exception

class ChiFragment : Fragment() {
    private var _binding: FragmentChiBinding ?= null
    private val binding get() = _binding!!

    private val alphaList = getChiAlphaList()

    private lateinit var chiDatas: List<DataTypes.ChiData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChiBinding.inflate(inflater, container, false)
        val dataPref = activity?.getSharedPreferences(CONST.Data_DB, Context.MODE_PRIVATE)
            ?: throw Exception("No Pref")
        chiDatas = Parser.prefChiDataParser(dataPref.getString(CONST.CHIDATA, "")?:"")
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            chiMainWrapper.setOnTouchListener { _, _ ->
                chiAlphaInput.clearFocus()
                chiDfInput.clearFocus()
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            }
        }
        initEditText()
        initSubmitBtn()
        initResetBtn()
    }

    private fun initEditText() = with(binding) {
        chiAlphaInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                chiAlphaInput.clearFocus()
                true
            }
            else false
        }
        chiAlphaInput.setOnFocusChangeListener { view, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        chiDfInput.setOnEditorActionListener { _, i, _ ->
            if(i == EditorInfo.IME_ACTION_DONE){
                chiDfInput.clearFocus()
                true
            }
            else false
        }
        chiDfInput.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun initSubmitBtn() = with(binding) {
        val dfRange = getDFRange()
        chiSubmitBtn.setOnClickListener {
            chiAlphaInput.clearFocus()
            chiDfInput.clearFocus()
            val alpha = chiAlphaInput.text.toString().toFloatOrNull() ?: -1.0f
            val df = chiDfInput.text.toString().toIntOrNull() ?: -1
            if(!alphaList.contains(alpha)){
                Toast.makeText(context, "잘못된 alpha값입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(!dfRange.contains(df)){
                Toast.makeText(context, "잘못된 자유도입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = chiDatas[10*(df-1)+alphaList.indexOf(alpha)]
            chiExplainText.text = "alpha값 ${alpha}에서 자유도 ${df}일 때의 χ값"
            chiResultText.text = result.chi.toString()

            chiAlphaInput.isEnabled = false
            chiDfInput.isEnabled = false
            chiSubmitBtn.isGone = true
            chiResetBtn.isGone = false
        }
    }

    private fun initResetBtn() = with(binding) {
        chiResetBtn.setOnClickListener {
            chiAlphaInput.isEnabled = true
            chiDfInput.isEnabled = true
            chiAlphaInput.text.clear()
            chiDfInput.text.clear()
            chiExplainText.text = ""
            chiResultText.text = ""
            chiSubmitBtn.isGone = false
            chiResetBtn.isGone = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}