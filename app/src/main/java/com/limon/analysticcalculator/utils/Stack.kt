package com.limon.analysticcalculator.utils

class Stack {
    private val stack = mutableListOf<Any>()

    fun push(item: Any) {
        stack.add(item)
    }

    fun pop(): Any {
        val item = stack.last()
        stack.remove(item)
        return item
    }

    fun getStack():List<Any> {
        return stack
    }

    fun getNow(): Any {
        if(stack.size == 0) {
            return 'a'
        }
        return stack.last()
    }

    fun getSize(): Int {
        return stack.size
    }

    fun isEmpty(): Boolean{
        if(stack.size == 0) {
            return true
        }
        return false
    }

    fun clear() {
        stack.clear()
    }
}