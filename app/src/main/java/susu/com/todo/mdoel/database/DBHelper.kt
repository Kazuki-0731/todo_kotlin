package susu.com.todo.mdoel.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import susu.com.todo.mdoel.action.SharedPref
import susu.com.todo.mdoel.entities.DataModel
import susu.com.todo.view.common.FrontConst
import java.lang.Exception

/**
 * SQLiteOpenHelperの拡張クラス
 */
class DBHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
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
            values.put(DBConstruct.DataEntry.ID, dataModel.id)
            values.put(DBConstruct.DataEntry.TODO_NAME, dataModel.todoList)
            values.put(DBConstruct.DataEntry.STATUS, dataModel.status)
            db.insert(DBConstruct.DataEntry.TABLE_NAME, null, values)
        } catch (e : Exception){
            // エラー内容をLog出力
            Log.d("debug", "insert Error")
            Log.d("debug", e.message)
        } finally {
            // 閉じる
            db.close()
        }
    }

    /**
     * レコード取得(昇順)
     */
    fun selectTODO() : Array<String> {
        val db = writableDatabase
        val shapre = SharedPref(context)
        var mutableList : MutableList<String> = mutableListOf()
        try {
            var cursor : Cursor
            if(FrontConst.SharedPref.ALL_TODO_LIST.value == shapre.listActiveSwitch){
                // クエリ
                cursor = db.query(
                    DBConstruct.DataEntry.TABLE_NAME,
                    arrayOf(DBConstruct.DataEntry.TODO_NAME),
                    null,
                    null,
                    null,
                    null,
                    DBConstruct.DataEntry.ID + " ASC",
                    null)
            } else {
                var status = ""
                // 表示用設定値 -> フラグ
                if(FrontConst.SharedPref.ACTIVE_TODO_LIST.value == shapre.listActiveSwitch){
                    status = "0"
                } else if(FrontConst.SharedPref.INACTIVE_TODO_LIST.value == shapre.listActiveSwitch){
                    status = "1"
                }
                Log.d("debug", "shapre.listActiveSwitch = " + shapre.listActiveSwitch)
                // クエリ
                cursor = db.query(
                    DBConstruct.DataEntry.TABLE_NAME,
                    arrayOf(DBConstruct.DataEntry.TODO_NAME),
                    DBConstruct.DataEntry.STATUS + " = ?",
                    arrayOf(status),
                    null,
                    null,
                    DBConstruct.DataEntry.ID + " ASC",
                    null)
            }
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
                DBConstruct.DataEntry.TABLE_NAME,
                arrayOf(DBConstruct.DataEntry.ID),
                null,
                null,
                null,
                null,
                DBConstruct.DataEntry.ID + " ASC",
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
            result = cursor.getInt(cursor.getColumnIndex(DBConstruct.DataEntry.STATUS))
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
     */
    fun updateState(id:String, status:String){
        val db = writableDatabase
        try {
            val values = ContentValues()
            values.put(DBConstruct.DataEntry.STATUS,status)
            // クエリ
            db.update(
                DBConstruct.DataEntry.TABLE_NAME,
                values,
                DBConstruct.DataEntry.ID + " = ?",
                arrayOf(id))
        } catch (e: Exception) {
            // エラー内容をLog出力
            Log.d("debug", "update Error")
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
                DBConstruct.DataEntry.TABLE_NAME,
                DBConstruct.DataEntry.ID + " = ?",
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
            "CREATE TABLE " + DBConstruct.DataEntry.TABLE_NAME + " (" +
                    DBConstruct.DataEntry.ID + " INTEGER ," +
                    DBConstruct.DataEntry.TODO_NAME + " TEXT ," +
                    DBConstruct.DataEntry.STATUS + " INTEGER)"
        /**
         * idをcountしてレコード数を取得
         */
        private const val SQL_COUNT_TODOS =  "SELECT " +
                "count(" + DBConstruct.DataEntry.ID + ") as cnt" +
                " FROM " + DBConstruct.DataEntry.TABLE_NAME
        /**
         * idを指定して対象レコードのStatusを取得
         */
        private const val SQL_STATUS_TODOS =  "SELECT " +
                DBConstruct.DataEntry.STATUS +
                " FROM " + DBConstruct.DataEntry.TABLE_NAME +
                " WHERE " + DBConstruct.DataEntry.ID + " = ?"
        /**
         * 削除
         */
        private const val SQL_DELETE_TODOS = "DROP TABLE IF EXISTS " + DBConstruct.DataEntry.TABLE_NAME
    }
}