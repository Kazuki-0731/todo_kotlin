package susu.com.todo.mdoel

import android.provider.BaseColumns

// DBの情報管理
object DBContract {
    // テーブル情報
    class DataEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "todo_table"
            const val ID = "id"
            const val TODO_NAME = "todo_name"
            const val STATUS = "status"
        }
    }
}