package susu.com.todo.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.content_main.*
import susu.com.todo.R
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.view.util.TodoListAdapter

/**
 * ListViewのFragment
 */
class TodoFragment  : Fragment(), TodoListAdapter.Listener {

    private var adapter : TodoListAdapter? = null
    var dataArray: MutableList<String>? = null

    // Fragment生成
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    // 表示後
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // DB初期化
        val dbhelper = DBHelper(activity!!.applicationContext)
        // DBから取得
        if(dbhelper.getCountID() != 0){
            dataArray = dbhelper.selectTODO().toMutableList()
        }
        // Adapter生成
        adapter = TodoListAdapter(activity!!.applicationContext, dataArray!!, this)
        // listViewに代入
        listView.adapter = adapter

        adapter!!.setListener(this)
    }

    /**
     * DBから取得してリロード
     */
    fun reload(context: Context, db : DBHelper){
        // DBから取得
        dataArray = db.selectTODO().toMutableList()

        // adapterセット
        adapter = TodoListAdapter(context, dataArray!!, this)

        adapter!!.notifyDataSetChanged()
        listView.adapter = adapter
    }

    override fun deleteRow(position: Int) {
        dataArray!!.removeAt(position)
        adapter!!.notifyDataSetChanged()
    }
}
