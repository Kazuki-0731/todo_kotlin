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
        // 配列初期化
        var dataArray: Array<String> = arrayOf()
        // DB初期化
        val dbhelper = DBHelper(activity!!.applicationContext)

        // DBから取得
        if(dbhelper.getCountID() != 0){
            dataArray = dbhelper.selectTODO()
        }

        // Adapter生成
        adapter = TodoListAdapter(activity!!.applicationContext, dataArray, this)
        // listViewに代入
        listView.adapter = adapter

        // 削除ボタン押下時

        // 挿入

        // 取り出し

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
