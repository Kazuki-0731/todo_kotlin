package susu.com.todo.mdoel.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import susu.com.todo.mdoel.entities.DataModel
import java.lang.Exception

/**
 * SQLiteOpenHelperの拡張クラス
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {
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
     * 全レコード取得(昇順)
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

    /**
     * IDの一覧を昇順で取得
     */
    fun getAllID() : Array<String> {
        val db = writableDatabase
        var mutableList : MutableList<String> = mutableListOf()
        try {
            // クエリ
            val cursor = db.query(
                DBContract.DataEntry.TABLE_NAME,
                arrayOf(DBContract.DataEntry.ID),
                null,
                null,
                null,
                null,
                DBContract.DataEntry.ID + " ASC",
                null)

            // 全データ配列化
            while (cursor.moveToNext()) {
                mutableList.add(cursor.getString(0))
                Log.d("debug", "id : " + cursor.getString(0))
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
     * Stateの一覧をIDの昇順で取得
     */
    fun getStatus(status : String) : Int {
        val db = writableDatabase
        var result = 0
        try {
            // クエリ
            val cursor = db.rawQuery(SQL_STATUS_TODOS, arrayOf(status))
            cursor.moveToFirst()
            result = cursor.getInt(cursor.getColumnIndex(DBContract.DataEntry.STATUS))
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

    /**
     * 対象レコードのStateを変更
     * update(DB_TABLE_NAME, values, "type=? AND date=? ", arrayOf(type.toString(),day.toString()))
     */
    fun updateState(id:String, status:String){
        val db = writableDatabase
        try {
            val values = ContentValues()
            values.put(DBContract.DataEntry.STATUS,status)
            // クエリ
            db.update(
                DBContract.DataEntry.TABLE_NAME,
                values,
                DBContract.DataEntry.ID + " = ?",
                arrayOf(id))
        } catch (e: Exception) {
            // エラー内容をLog出力
            Log.d("debug", "select Error")
            Log.d("debug", e.message)
        } finally {
            // 閉じる
            db.close()
        }
    }

    /**
     * １レコード削除
     */
    fun deleteRecord(id : String) {
        val db = writableDatabase
        try {
            // クエリ
            db.delete(
                DBContract.DataEntry.TABLE_NAME,
                DBContract.DataEntry.ID + " = ?",
                arrayOf(id))
        } catch (e: Exception) {
            // エラー内容をLog出力
            Log.d("debug", "delete Error")
            Log.d("debug", e.message)
        } finally {
            // 閉じる
            db.close()
        }
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
         * idをcountしてレコード数を取得
         */
        private const val SQL_COUNT_TODOS =  "SELECT " +
                "count(" + DBContract.DataEntry.ID + ") as cnt" +
                " FROM " + DBContract.DataEntry.TABLE_NAME
        /**
         * idを指定して対象レコードのStatusを取得
         */
        private const val SQL_STATUS_TODOS =  "SELECT " +
                DBContract.DataEntry.STATUS +
                " FROM " + DBContract.DataEntry.TABLE_NAME +
                " WHERE " + DBContract.DataEntry.ID + " = ?"
        /**
         * 削除
         */
        private const val SQL_DELETE_TODOS = "DROP TABLE IF EXISTS " + DBContract.DataEntry.TABLE_NAME
    }
}