package susu.com.todo.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import susu.com.todo.R
import susu.com.todo.mdoel.DBHelper

class TodoListAdapter(private val context: Context,
                      private val sortedList: Array<String>,
                      private val fragment: TodoFragment
) : BaseAdapter() {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
         * actice -> inactive
         * この時に文字に横線を引く
         *
         * inactice -> active
         * この時に文字の横線を解除
         *
         */
        val imageCheck = view.findViewById<ImageView>(R.id.image_check)
        imageCheck.setOnClickListener{
            imageCheck.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check_box_black_24dp, null))
        }

        /**
         * ゴミ箱アイコン
         * このアイコンクリックで１行削除する
         */
        //image_delete
        val imageDelete = view.findViewById<ImageView>(R.id.image_delete)
        imageDelete.setOnClickListener{
            // ダイアログを閉じないで新規ダイアログ表示
            val warningDialog = InputTextDialog(context)
            warningDialog.dialogTitle = "▲ 警告 ▲"
            warningDialog.dialogMessage = "削除してもよろしいでしょうか？"
            warningDialog.editText = null
            warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->
                // DB初期化
                val dbhelper = DBHelper(context)
                // 削除実行
                dbhelper.deleteRecord(position)
                // リロード
                fragment.reload(context, dbhelper)
            }
            warningDialog.isCancelButton = true
            // ダイアログ表示
            warningDialog.openDialog(fragment.fragmentManager!!)
        }

        // 返却
        return view
    }

    // TODO あとで消す
    // 末尾に追加
    fun add(item:String) {
        this.sortedList.plus(item)
    }
}
