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
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

import kotlinx.android.synthetic.main.activity_main.*
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.mdoel.entities.DataModel
import susu.com.todo.view.fragment.util.InputTextDialog
import susu.com.todo.view.fragment.TodoFragment

/**
 * 右下にあるボタン押下でTODOのリストを追加するためのPopUp表示
 * PopUpでテキスト入力し、追加ボタン押下でリスト追加
 * その際に画面リロード
 *
 * validationを設ける
 * 絵文字、半角全角入力可能(文字数のみ)
 * 文字数は最大10文字程度(それ以上だと画面の横幅を超えてしまう)
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
 *
 * TODO アプリアイコン変えたい(希望)
 */
class MainActivity : AppCompatActivity() {
//    private var dataModel = DataModel("")
    private val context : Context = this

    // Todo一覧のインスタンスを保持
    private lateinit var todoFragment: TodoFragment

    // アニメーション定数
    enum class FloatingActionState {
        NORMAL,
        ANIMATED
    }
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
            val dialog = InputTextDialog(context)
            // ダイアログ用にカスタムクラスに設定している
            dialog.dialogTitle = "テキスト入力"
            dialog.dialogMessage = "TODO項目を入力してください"
            dialog.dialogTextData = ""
            // OKボタン
            dialog.onOkButtonClickListener = View.OnClickListener {
                // 入力テキストを保持
                val textData = dialog.dialogTextData

                // DBの保存できる上限なのかチェック
                if(dbhelper.getCountID() >= 100){
                    // ダイアログを閉じないで新規ダイアログ表示
                    val warningDialog = InputTextDialog(context)
                    warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                    warningDialog.dialogMessage = "最大登録件数を超えるため、１件以上削除してください"
                    warningDialog.editText = null
                    warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                    warningDialog.isOkButton = true
                    warningDialog.isCancelButton = false
                    // ダイアログ表示
                    warningDialog.openDialog(supportFragmentManager)
                } else {
                    // 文字数が10文字以上なのか判定
                    if(textData.length > 10){
                        // ダイアログを閉じないで新規ダイアログ表示
                        val warningDialog =
                            InputTextDialog(context)
                        warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                        warningDialog.dialogMessage = "最大文字数を超えるため、10文字以下で入力してください"
                        warningDialog.editText = null
                        warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                        warningDialog.isOkButton = true
                        warningDialog.isCancelButton = false
                        // ダイアログ表示
                        warningDialog.openDialog(supportFragmentManager)
                    } else {
                        // DBに保存
                        val todoRecord = DataModel(
                            dbhelper.getCountID(),
                            textData,
                            0 // 初期値をInactive(0)とする
                        )
                        // 挿入
                        dbhelper.insertTODO(todoRecord)
                        // 保存
                        Snackbar.make(view,"$textData をTODOリストへ追加しました", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        // ListViewリロード
                        todoFragment.reload(context, dbhelper)
                        // 逆回転
                        if (state == FloatingActionState.ANIMATED && !closingAnimation.isRunning) {
                            closeFloatingActionFragment()
                        }
                        // Dialogを閉じる
                        dialog.dialogDismiss()
                    }
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
