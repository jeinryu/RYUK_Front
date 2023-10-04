package org.techtown.ryuk

import kotlin.properties.Delegates

class ToDo {
    var content : String = ""
    var check : Boolean = false
    var type : Int = 0
    // var id by Delegates.notNull<Int>()

    companion object {
        var todoCount : Int = 0
        var todoList = mutableListOf<ToDo>()
        var checkedCount : Int = 0
//        fun currentTodoCount() : Int {
//            return todoCount
//        }
//        fun currentTodoList(): MutableList<ToDo> {
//            return todoList
//        }
        fun init() {
            todoCount = 0
            checkedCount = 0
            todoList.clear()
        }

    }
    fun reverseCheck() {
        check = !check
        when(check) {
            true -> checkedCount++
            false -> checkedCount--
        }
    }
    constructor(content: String, check: Boolean, type: Int) {
        this.content = content
        this.check = check
        this.type = type
        // this.id = id
        todoCount++
        todoList.add(this)
    }
}