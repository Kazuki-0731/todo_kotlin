package susu.com.todo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import susu.com.todo.controller.action.UserIO
import susu.com.todo.mdoel.dictionary.SharedPref
import susu.com.todo.view.animation.Floating
import susu.com.todo.view.fragment.TodoFragment

/**
 * 右下にあるボタン押下でTODOのリストを追加するためのPopUp表示
 * PopUpでテキスト入力し、追加ボタン押下でリスト追加
 * その際に画面リロード
 *
 * validationを設ける
 * 絵文字、半角全角入力可能(文字数のみ)
 * 文字数は最大50文字程度
 *
 * 文字数を超える場合はアラート表示
 *
 * 削除ボタン押下で削除
 * list_itemを作成し、そこに削除ボタンを設ける方が早い
 * (RecyclerViewを利用して右にスワイプしたら削除ボタン表示)
 * (※ここに関してはRecyclerViewでなくても良い)
 *
 * チェックボックスOnでinactive
 * チェックボックスOffでactive
 * inactiveとは、横線を付ける
 * activeとは、横線を外す
 *
 * SQLiteOpenHelperの拡張カスタムクラスでデータ保存
 */
class MainActivity : AppCompatActivity() {
    // 初期表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Fragment生成
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, TodoFragment.getInstance())
            transaction.commit()
        }

        // 設定値保存用
        SharedPref(this)

        // Controllerを初期化
        UserIO(this, supportFragmentManager)

        // appContextを保持
        Floating(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("debug","onPause()")
    }
}
