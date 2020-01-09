package susu.com.todo.view.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import susu.com.todo.R
import susu.com.todo.mdoel.database.DBConstruct
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.view.fragment.TodoFragment

class TodoListAdapter(private val context: Context,
                      private val sortedList: MutableList<String>,
                      private val fragment: TodoFragment
) : BaseAdapter() {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listener: Listener? = null

    override fun getCount(): Int {
        return sortedList.count()
    }

    override fun getItem(position: Int): String {
        return sortedList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 表示するレイアウト取得
        val view = layoutInflater.inflate(R.layout.todo_list_item, parent, false)

        //各TextView
        val todoText = view.findViewById<TextView>(R.id.todo_text)
        todoText.text = sortedList[position]

        /**
         * チェックボックス押下時
         * DBに現在とは反対の状態を保存する
         */
        val imageCheck = view.findViewById<ImageView>(R.id.image_check)
        imageCheck.setOnClickListener{
            // DBオブジェクト生成
            val dbhelper = DBHelper(context)
            // ListViewのpositionから、レコードIDを取得
            val allIdList = dbhelper.getAllID()
            // 元々のStateを取得
            val targetStatus = dbhelper.getStatus(allIdList[position])
            // DBのstatus判定し、現在とは反対の値を格納
            if(DBConstruct.CheckStatus.INACTIVE.status == targetStatus){
                dbhelper.updateState(allIdList[position], DBConstruct.CheckStatus.ACTIVE.status.toString())
            } else if(DBConstruct.CheckStatus.ACTIVE.status == targetStatus){
                dbhelper.updateState(allIdList[position], DBConstruct.CheckStatus.INACTIVE.status.toString())
            }
            // listViewリロード
            fragment.reload(context, dbhelper)
        }

        /**
         * ゴミ箱アイコン
         * このアイコンクリックで１行削除する
         */
        val imageDelete = view.findViewById<ImageView>(R.id.image_delete)
        imageDelete.setOnClickListener{
            // ダイアログを閉じないで新規ダイアログ表示
            val warningDialog = InputTextDialog(context)
            warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
            warningDialog.dialogMessage = "削除してもよろしいでしょうか？"
            warningDialog.editText = null
            warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->
                // DBオブジェクト生成
                val dbhelper = DBHelper(context)
                // ListViewのositionから、レコードIDを取得
                val allIdList = dbhelper.getAllID()
                // 対象レコードの削除実行
                dbhelper.deleteRecord(allIdList[position])
//                // listViewリロード
//                fragment.reload(context, dbhelper)
//                fragment.dataArray!!.removeAt(position)
                listener!!.deleteRow(position)
            }
            warningDialog.isCancelButton = true
            // ダイアログ表示
            warningDialog.openDialog(fragment.fragmentManager!!)
        }

        /**
         * DBから値を取得し横線を付ける
         * これは画面描画時に行う
         *
         * actice -> inactive
         * チェックボックスをOn
         * 文字に横線を引く
         *
         * inactice -> active
         * チェックボックスをOff
         * この時に文字の横線を解除
         */
        // DBオブジェクト生成
        val dbhelper = DBHelper(context)
        // ListViewのpositionから、レコードIDを取得
        val allIdList = dbhelper.getAllID()
        // 元々のStateを取得
        val targetStatus = dbhelper.getStatus(allIdList[position])
        // DBのstatus判定
        if(DBConstruct.CheckStatus.INACTIVE.status == targetStatus){
            // Off -> On
            imageCheck.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check_box_black_24dp, null))
            // 文字に横線
            todoText.inactiveLine()
        } else if(DBConstruct.CheckStatus.ACTIVE.status == targetStatus){
            // On -> Off
            imageCheck.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check_box_outline_blank_black_24dp, null))
            // 文字に横線解除
            todoText.activeLine()
        }
        // 返却
        return view
    }

    // TexgtViewに横線
    private fun TextView.inactiveLine() {
        paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
        paint.isAntiAlias = true
    }

    // TextViewの横線解除
    private fun TextView.activeLine() {
        paint.flags = paint.flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        paint.isAntiAlias = true
    }

    interface Listener {
        fun deleteRow(position: Int)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }
}
