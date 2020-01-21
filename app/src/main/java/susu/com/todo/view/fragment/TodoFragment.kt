package susu.com.todo.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.content_main.*
import susu.com.todo.R
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.view.util.TodoListAdapter


/**
 * ListViewのFragment
 */
class TodoFragment : Fragment() {

    // 静的領域
    companion object {
        // 遅延宣言
        private var instance: TodoFragment = TodoFragment()
        // シングルトンなインスタンス取得
        fun getInstance(): TodoFragment {
            return instance
        }
    }

    private var adapter : TodoListAdapter? = null

    // Fragment生成
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // 再描画防止(onCreateは1度しか呼ばれない)
        retainInstance = true
        return inflater.inflate(R.layout.content_main, container, false)
    }

    // 表示後
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 配列初期化
        var dataArray: Array<String> = arrayOf()
        // DB初期化
        val dbhelper = DBHelper(activity!!.applicationContext)
        // DBから
        if(dbhelper.getMaxID() != 0){
            dataArray = dbhelper.selectTODO()
        }
        // Adapter生成
        adapter = TodoListAdapter(activity!!.applicationContext, dataArray, this)
        // listViewに代入
        listView.adapter = adapter
        // 長押しイベント付与
        listView.setOnItemLongClickListener { parent, view, position, id ->
            // 対象セルの文字列表示
            val listView: ListView = parent as ListView
            val str = listView.getItemAtPosition(position) as String
            Toast.makeText(activity!!.applicationContext, "$str", Toast.LENGTH_LONG).show()
            true
        }
    }

    /**
     * DBから取得してリロード
     */
    fun reload(context: Context, db : DBHelper){
        // DBから取得
        var dataArray = db.selectTODO()

        // adapterセット
        adapter = TodoListAdapter(context, dataArray, this)
        adapter!!.notifyDataSetChanged()
        listView.adapter = adapter
    }
}
