package susu.com.todo.mdoel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception

/**
 * SQLiteOpenHelperの拡張クラス
 * TODO idがどこまで最大長なのか知る必要ある
 * -> これによっては、ListViewで表示するmaxが決まる
 * また、Validationも決まる
 *
 * TODO Stringの最大長も調べる
 * -> 表示の最大
 * また、Validationも決まる
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    @Throws(SQLiteException::class)

    /**
     * データ追加
     */
    fun insertTODO(dataModel: DataModel) {
        val db = writableDatabase
        val values = ContentValues()
        try {
            values.put(DBContract.DataEntry.ID, dataModel.id)
            values.put(DBContract.DataEntry.TODO_NAME, dataModel.todoList)
            values.put(DBContract.DataEntry.STATUS, dataModel.status)
            db.insert(DBContract.DataEntry.TABLE_NAME, null, values)
        } catch (e : Exception){
            // エラー内容をLog出力
            Log.d("debug", "insert Error")
            Log.d("debug", e.message)
        }
    }

    /**
     * 全レコード取得(降順)
     */
    fun selectTODO() : Array<String> {
        val db = writableDatabase
        var mutableList : MutableList<String> = mutableListOf()
        try {
            // クエリ
            val cursor = db.query(
                DBContract.DataEntry.TABLE_NAME,
                arrayOf(DBContract.DataEntry.TODO_NAME),
                null,
                null,
                null,
                null,
                DBContract.DataEntry.ID + " ASC",
                null)

            // 全データ配列化
            while (cursor.moveToNext()) {
                mutableList.add(cursor.getString(0))
                Log.d("debug", "todo_name : " + cursor.getString(0))
            }
        } catch (e: Exception) {
            // エラー内容をLog出力
            Log.d("debug", "select Error")
            Log.d("debug", e.message)
        } finally {
            // 閉じる
            db.close()
        }
        return mutableList.toTypedArray()
    }

    /**
     * レコード数を取得する
     */
    fun getCountID() : Int {
        val db = writableDatabase
        var result = 0
        try {
            // クエリ
            val cursor = db.rawQuery(SQL_COUNT_TODOS, null)
            cursor.moveToFirst()
            result = cursor.getInt(cursor.getColumnIndex("cnt"))
        } catch (e: Exception) {
            // エラー内容をLog出力
            Log.d("debug", "select Error")
            Log.d("debug", e.message)
        } finally {
            // 閉じる
            db.close()
        }
        return result
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TODOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_TODOS)
        db?.execSQL(SQL_CREATE_TODOS)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    // DB操作用の定数
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TODO.db"

        /**
         * テーブル生成
         */
        private const val SQL_CREATE_TODOS =
            "CREATE TABLE " + DBContract.DataEntry.TABLE_NAME + " (" +
                    DBContract.DataEntry.ID + " INTEGER ," +
                    DBContract.DataEntry.TODO_NAME + " TEXT ," +
                    DBContract.DataEntry.STATUS + " INTEGER)"

        /**
         * TODOの内容を取得(降順)
         * TODO 消すかも？
         */
        private const val SQL_SELECT_TODOS =  "SELECT " +
                DBContract.DataEntry.TODO_NAME +
                " FROM " + DBContract.DataEntry.TABLE_NAME

        /**
         * idをcountしてレコード数を取得
         */
        private const val SQL_COUNT_TODOS =  "SELECT " +
                "count(" + DBContract.DataEntry.ID + ") as cnt" +
                " FROM " + DBContract.DataEntry.TABLE_NAME

        /**
         * 削除
         */
        private const val SQL_DELETE_TODOS = "DROP TABLE IF EXISTS " + DBContract.DataEntry.TABLE_NAME
    }
}