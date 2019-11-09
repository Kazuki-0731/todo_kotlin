package susu.com.todo.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import susu.com.todo.R

class TodoListAdapter(private val context: Context,
                      private val sortedList: Array<String>) : BaseAdapter() {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var prefs : SharedPreferences

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

        val image_check = view.findViewById<ImageView>(R.id.image_check)
        image_check.setOnClickListener{
            image_check.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check_box_black_24dp, null))
        }


        // 返却
        return view
    }

    // 末尾に追加
    fun add(item:String) {
        this.sortedList.plus(item)
    }
}
