package com.limon.analysticcalculator.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.limon.analysticcalculator.databinding.FragmentCalcBinding
import com.limon.analysticcalculator.utils.Stack
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

class CalcFragment : Fragment() {
    private var _binding: FragmentCalcBinding ?= null
    private val binding get() = _binding!!

    private val priority = mapOf('(' to 0, ')' to 1, '√' to 2, 'l' to 3,  'n' to 3, '^' to 4, '%' to 5, '÷' to 6, '×' to 7, '-' to 8, '+' to 9)

    private val stack: Stack = Stack()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtn()
    }

    @SuppressLint("SetTextI18n")
    private fun initBtn() = with(binding) {
        val numBtnList = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, eBtn)
        val charBtnList = listOf(dotBtn, modBtn, divBtn, mulBtn, minusBtn, plusBtn, bracketOpenBtn, bracketCloseBtn, powerBtn)
        val btnList = listOf(rootBtn, lnBtn)

        for(btn in numBtnList) {
            btn.setOnClickListener {
                var now = calcResultText.text.toString()
                if(now.length >= 40) {
                    Toast.makeText(context, "더이상 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if(now == "0") {
                    now = ""
                } else if(now.last() == 'e') {
                    now += '^'
                }
                calcResultText.text = now + btn.text.toString()
            }
        }

        for(btn in charBtnList) {
            btn.setOnClickListener {
                var now = calcResultText.text.toString()
                if(now.length >= 40) {
                    Toast.makeText(context, "더이상 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if(now.last() == '.') {
                    Toast.makeText(context, "소수점 뒤에는 연산자를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if(btn == bracketOpenBtn) {
                    if(now == "0") now = ""
                    else if (listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e').contains(now.last())) {
                        Toast.makeText(context, "숫자 뒤에는 괄호를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else if(btn == bracketCloseBtn) {
                    if(now == "0") {
                        Toast.makeText(context, "맨 앞에는 닫는 괄호를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else if(!listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e').contains(now.last())) {
                    Toast.makeText(context, "연산자는 연달아 올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if(btn == dotBtn && now.last() == 'e') {
                    Toast.makeText(context, "e 뒤에는 소수점이 올 수 업습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                calcResultText.text = now + btn.text.toString()
            }
        }

        for(btn in btnList) {
            btn.setOnClickListener {
                var now = calcResultText.text.toString()
                if(now.length >= 40) {
                    Toast.makeText(context, "더이상 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if(now == "0") {
                    now = ""
                } else if(listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e').contains(now.last())) {
                    Toast.makeText(context, "숫자 뒤에는 올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                calcResultText.text = now + btn.text.toString() + '('
            }
        }

        acBtn.setOnClickListener { calcResultText.text = "0" }
        backBtn.setOnClickListener {
            val now = calcResultText.text.toString()
            if (now.length <= 1) {
                calcResultText.text = "0"
                return@setOnClickListener
            }
            calcResultText.text = calcResultText.text.toString().dropLast(1)
        }
        submitBtn.setOnClickListener {
            val result = calc(calcResultText.text.toString())
            if (result!=null){
                calcResultText.text = result.toString()
            }
        }
    }

    private fun calc(centerCalc: String): Double? {
        val backCalc = parser(centerCalc) ?: return null
        stack.clear()
        for(i in backCalc) {
            if (i::class == Double::class) {
                stack.push(i)
            } else {
                when(i) {
                    '√' -> {
                        stack.push(sqrt(stack.pop() as Double))
                    }
                    'n' -> {
                        stack.push(ln(stack.pop() as Double))
                    }
                    '^' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        stack.push(num2.pow(num1))
                    }
                    '%' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        if(num1 == 0.0) {
                            Toast.makeText(context, "0으로는 나눌 수 없습니다.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        stack.push(num2 % num1)
                    }
                    '÷' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        if(num1 == 0.0) {
                            Toast.makeText(context, "0으로는 나눌 수 없습니다.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        stack.push(num2 / num1)
                    }
                    '×' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        stack.push(num2 * num1)
                    }
                    '-' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        stack.push(num2 - num1)
                    }
                    '+' -> {
                        val num1 = stack.pop() as Double
                        val num2 = stack.pop() as Double
                        stack.push(num2 + num1)
                    }
                }
            }
        }
        if(stack.getSize()!=1){
            throw Error("계산 과정에서 문제가 발생했습니다.")
        }
        return stack.pop() as Double
    }

    private fun parser(centerCalc: String):List<Any>? {
        stack.clear()
        val backCalc = mutableListOf<Any>()
        var nowNum = ""
        var isInBracket = 0
        for(i in centerCalc.toList()) {
            val nowPriority = priority[i]
            if(nowPriority == null) {
                nowNum += i
                continue
            }
            if(nowNum == "e") {
                backCalc.add(exp(1.0))
                nowNum = ""
            } else if(nowNum!=""){
                backCalc.add(nowNum.toDouble())
                nowNum = ""
            }
            when(i) {
                '(' -> {
                    stack.push(i)
                    isInBracket += 1
                }
                ')' -> {
                    var now = stack.pop()
                    while (now != '(') {
                        backCalc.add(now)
                        if(stack.isEmpty()){
                            Toast.makeText(context, "괄호의 개수가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                        now = stack.pop()
                    }
                    val top = stack.getNow()
                    if (top == 'n' || top == '√') {
                        backCalc.add(stack.pop())
                    }
                    isInBracket -= 1
                }
                'n', '√' -> {
                    stack.push(i)
                    isInBracket += 1
                }
                '^' -> {
                    stack.push(i)
                }
                '%', '÷', '×', '-', '+' -> {
                    if((priority[stack.getNow() as Char] ?: -1) >= nowPriority) {
                        backCalc.add(stack.pop())
                    }
                    stack.push(i)
                }
            }
        }
        if (stack.getStack().contains('(')) {
            Toast.makeText(context, "괄호의 개수가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            return null
        }
        if(nowNum == "e") {
            backCalc.add(exp(1.0))
        } else if(nowNum!=""){
            backCalc.add(nowNum.toDouble())
        }
        if(!stack.isEmpty()) backCalc.add(stack.pop())
        return backCalc
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}