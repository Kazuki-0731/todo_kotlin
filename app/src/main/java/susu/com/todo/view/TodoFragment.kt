package susu.com.todo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.content_main.*
import susu.com.todo.R
import susu.com.todo.mdoel.DBHelper
import susu.com.todo.mdoel.SharedPref

/**
 * ListViewのFragment
 */
class TodoFragment  : Fragment() {

    private var adapter : TodoListAdapter? = null

    // Fragment生成
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    //表示後
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        var shaPref = SharedPref(activity!!.applicationContext)
        val dbhelper = DBHelper(activity!!.applicationContext)

        // shaPrefから取得
//        val dataArray = arrayOf("Kotlin","Android","iOS","Swift","Java")
        var dataArray: Array<String> = arrayOf()
//        if(shaPref.todoListItem != null){
//            dataArray = shaPref.todoListItem!!.parseCsv()
//        }

        // DBから取得
        if(dbhelper.getCountID() != 0){
            dataArray = dbhelper.selectTODO()
        }

        // Adapter生成
        adapter = TodoListAdapter(activity!!.applicationContext, dataArray)
        listView.adapter = adapter

        // 削除ボタン押下時

        // 挿入

        // 取り出し

    }

    /**
     * CSV 形式の文字列をカンマで分割します。
     * 各要素の先頭・末尾の空白は削除されます。
     */
    private fun String.parseCsv() : Array<String> {
//        return split(",").map { it.trim() }.toMutableList()
        return split(",").map { it.trim() }.toTypedArray()
    }

    /**
     * sharePreferencesから取得してリロード
     */
    fun reload(context : Context, shaPref: SharedPref){
        // データセット
        var dataArray = shaPref.todoListItem!!.parseCsv()

        // 毎回全リストを入れ替えになっているので、追加されたTODOだけを
        // 追加して再描画する形に変えたほうがメモリ管理的にも良いはず。
        // TodoListAdapterにaddメソッド作るなりして保持しているListに追加してあげればいけるはず
        adapter = TodoListAdapter(context, dataArray)
        adapter!!.notifyDataSetChanged()
        listView.adapter = adapter
    }

    /**
     * DBから取得してリロード
     */
    fun reload(context: Context, db : DBHelper){
        // DBから取得
        var dataArray = db.selectTODO()

        // adapterセット
        adapter = TodoListAdapter(context, dataArray)
        adapter!!.notifyDataSetChanged()
        listView.adapter = adapter
    }
}
