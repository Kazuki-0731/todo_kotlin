package susu.com.todo

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

import kotlinx.android.synthetic.main.activity_main.*
import susu.com.todo.mdoel.DBHelper
import susu.com.todo.mdoel.DataModel
import susu.com.todo.mdoel.SharedPref
import susu.com.todo.view.InputTextDialog
import susu.com.todo.view.TodoFragment

/**
 * 右下にあるボタン押下でTODOのリストを追加するためのPopUp表示
 * PopUpでテキスト入力し、追加ボタン押下でリスト追加
 * その際に画面リロード
 *
 * validationを設ける
 * 絵文字、半角全角入力可能(文字数のみ)
 * 文字数は最大20文字程度(それ以上だと画面の横幅を超えてしまう)
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
 * sharedPreferenceでデータ保存
 *
 * TODO アプリアイコン変えたい(希望)
 */
class MainActivity : AppCompatActivity() {
    // アニメーション定数
    enum class FloatingActionState {
        NORMAL, ANIMATED
    }

//    private var dataModel = DataModel("")
    private val context : Context = this

    // Todo一覧のインスタンスを保持
    private lateinit var todoFragment:TodoFragment

    // アニメーションプロパティ
    private lateinit var state: FloatingActionState
    private lateinit var openingAnimation: Animator
    private lateinit var closingAnimation: Animator

    // 初期表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Todoフラグメント初期化
        todoFragment = TodoFragment()

        // 辞書クラス初期化
        var shaPref = SharedPref(this)
//        shaPref.todoListItem = "Kotlin,Android,iOS,Swift,Java"

        // DBHelperクラス初期化
        val dbhelper = DBHelper(this)
        Log.d("debug", "表示テスト")

        // Fragment生成
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, todoFragment)
            transaction.commit()
        }

        // アニメーションの状態初期化
        state = FloatingActionState.NORMAL

        // アニメーションオブジェクト生成
        openingAnimation = createOpenFloatingActionButton()
        closingAnimation = createCloseFloatingActionButton()

        // 右下のTODO追加ボタン押下
        fab.setOnClickListener { view ->
            // 回転
            if (state == FloatingActionState.NORMAL && !openingAnimation.isRunning) {
                openFloatingActionButton()
            }

            // ダイアログ生成
            val dialog : InputTextDialog = InputTextDialog(this)
            // ダイアログ用にカスタムクラスに設定している
            dialog.dialogTitle = "テキスト入力"
            dialog.dialogMessage = "TODO項目を入力してください"
            dialog.dialogTextData = ""
            // OKボタン
            dialog.onOkClickListener = DialogInterface.OnClickListener { _, _->
                // TODO 文字列が20文字以下のValidationを設ける?
                // 入力テキストを保存
                val textData = dialog.dialogTextData
                var shaPref = SharedPref(context)
                shaPref.todoListItem = shaPref.todoListItem.plus(",").plus(textData)

                // DBに保存
                // TODO 最後のID取得
                val todoRecord = DataModel(
                    dbhelper.getCountID(),
//                    0,
                    textData,
                    0 // 初期値を0とする
                )

                // 挿入
                val result = dbhelper.insertTODO(todoRecord)

                // 保存成功
                if(result){
                    // 通知
                    Snackbar.make(view,"$textData をTODOリストへ追加しました", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                } else {
                    // 保存失敗
                }

                // ListViewリロード
//                todoFragment.reload(context, shaPref)
                todoFragment.reload(context, dbhelper)

                // 逆回転
                if (state == FloatingActionState.ANIMATED && !closingAnimation.isRunning) {
                    closeFloatingActionFragment()
                }
            }
            // キャンセルボタン
            dialog.onCancelClickListener = DialogInterface.OnClickListener { _, _ ->
                // 逆回転
                if (state == FloatingActionState.ANIMATED && !closingAnimation.isRunning) {
                    closeFloatingActionFragment()
                }
            }
            dialog.isCancelButton = true
            // ダイアログ表示
            dialog.openDialog(supportFragmentManager)
        }
    }

    // メニューをActivity上に設置する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 設定ボタン押下時イベント
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        if (openingAnimation.isRunning) {
            openingAnimation.cancel()
        }
        if (closingAnimation.isRunning) {
            closingAnimation.cancel()
        }
    }

    // 開く動作オブジェクト
    private fun createOpenFloatingActionButton(): Animator {
        val anim = AnimatorInflater.loadAnimator(applicationContext, R.animator.fab_open)
        anim.setTarget(fab)
        anim.interpolator = DecelerateInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                state = FloatingActionState.ANIMATED
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.end()
                state = FloatingActionState.ANIMATED
            }
        })
        return anim
    }

    // 閉じる動作オブジェクト
    private fun createCloseFloatingActionButton(): Animator {
        val anim = AnimatorInflater.loadAnimator(applicationContext, R.animator.fab_close)
        anim.setTarget(fab)
        anim.interpolator = AccelerateInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                state = FloatingActionState.NORMAL
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.end()
                state = FloatingActionState.NORMAL
            }
        })
        return anim
    }

    // 開くアニメーション
    private fun openFloatingActionButton() {
        openingAnimation.start()
    }

    // 閉じるアニメーション
    private fun closeFloatingActionFragment() {
        closingAnimation.start()
    }
}
