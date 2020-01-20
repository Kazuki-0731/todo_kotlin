package susu.com.todo

import android.animation.Animator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import susu.com.todo.mdoel.action.SharedPref
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.mdoel.entities.DataModel
import susu.com.todo.view.animation.Floating
import susu.com.todo.view.common.FrontConst
import susu.com.todo.view.util.InputTextDialog
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
    private val context : Context = this

    // Todo一覧のインスタンスを保持
    private lateinit var todoFragment: TodoFragment

    // アニメーションプロパティ
    private lateinit var floating : Floating
    private lateinit var openingAnimation: Animator
    private lateinit var closingAnimation: Animator

    // 初期表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // appContextを保持
        floating = Floating(this)

        // Todoフラグメント初期化
        todoFragment = TodoFragment()

        // DBHelperクラス初期化
        val dbhelper = DBHelper(this)

        // 設定値保存用
        val shapre = SharedPref(this)

        // Fragment生成
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, todoFragment)
            transaction.commit()
        }

        // アニメーションの状態初期化
        Floating.state = Floating.FloatingActionState.NORMAL

        // アニメーションオブジェクト生成
        openingAnimation = floating.createOpenFloatingActionButton()
        closingAnimation = floating.createCloseFloatingActionButton()

        // 設定値から読み出して初期表示
        switchFilterIcon(shapre, true)

        /**
         * onClickのリスナー群
         */
        // Active/Inactive切替ボタン押下処理
        fab_active_inactive.setOnClickListener {
            // 設定値から読み出して表示切替
            switchFilterIcon(shapre, false)
            // ListViewリロード
            todoFragment.reload(context, dbhelper)
        }

        // 右下のTODO追加ボタン押下処理
        fab_add.setOnClickListener { view ->
            // 回転
            if (Floating.state == Floating.FloatingActionState.NORMAL && !openingAnimation.isRunning) {
                openFloatingActionButton()
            }
            Log.d("debug", "Floating.openingAnimation.isRunning : ".plus(openingAnimation.isRunning))
            Log.d("debug", "FloatingFloating.state : ".plus(Floating.state))

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

                /**
                 * DBの保存できる上限なのかチェック
                 * 100件目まで登録可能
                 * 101件からは登録できない
                 */
                if(dbhelper.getMaxID() >= FrontConst.Limit.LISTVIEW_REGISTER_LIMIT.value){
                    // ダイアログを閉じないで新規ダイアログ表示
                    val warningDialog = InputTextDialog(context)
                    warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                    warningDialog.dialogMessage = "最大登録件数(" +
                            FrontConst.Limit.LISTVIEW_REGISTER_LIMIT.value +
                            ")を超えるため、１件以上削除してください"
                    warningDialog.editText = null
                    warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                    warningDialog.isOkButton = true
                    warningDialog.isCancelButton = false
                    // ダイアログ表示
                    warningDialog.openDialog(supportFragmentManager)
                } else {
                    /**
                     * 文字数判定
                     * あまりにも文字が長いと見栄えが悪いため
                     * 50文字まで入力可能
                     * 51文字目からは入力できない仕様
                     * 逆に短すぎるとTODOとしての機能を果たせないため、ある程度余裕を持たせている
                     */
                    if(textData.length > FrontConst.Limit.UPPER_LIMIT_OF_INPUT_VALUE.value){
                        // ダイアログを閉じないで新規ダイアログ表示
                        val warningDialog = InputTextDialog(context)
                        warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                        warningDialog.dialogMessage = "最大文字数を超えるため、" +
                                FrontConst.Limit.UPPER_LIMIT_OF_INPUT_VALUE.value +
                                "文字以下で入力してください"
                        warningDialog.editText = null
                        warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                        warningDialog.isOkButton = true
                        warningDialog.isCancelButton = false
                        // ダイアログ表示
                        warningDialog.openDialog(supportFragmentManager)
                    } else if(textData.isEmpty()){
                        // ダイアログを閉じないで新規ダイアログ表示
                        val warningDialog = InputTextDialog(context)
                        warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                        warningDialog.dialogMessage = "1文字以上で入力してください"
                        warningDialog.editText = null
                        warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                        warningDialog.isOkButton = true
                        warningDialog.isCancelButton = false
                        // ダイアログ表示
                        warningDialog.openDialog(supportFragmentManager)
                    } else {
                        // DBに保存
                        val todoRecord = DataModel(
                            dbhelper.getMaxID(),
                            textData,
                            FrontConst.Init.INITIAL_VALUE_OF_STATUS_WHEN_TODO_IS_ADDED.value // 初期値をInactive(0)とする
                        )
                        // 挿入
                        dbhelper.insertTODO(todoRecord)
                        // 保存
                        Snackbar.make(view,"$textData をTODOリストへ追加しました", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        // ListViewリロード
                        todoFragment.reload(context, dbhelper)
                        // 逆回転
                        if (Floating.state == Floating.FloatingActionState.ANIMATED && !closingAnimation.isRunning) {
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
                if (Floating.state == Floating.FloatingActionState.ANIMATED && !closingAnimation.isRunning) {
                    closeFloatingActionFragment()
                }
            }
            dialog.isCancelButton = true
            // ダイアログ表示
            dialog.openDialog(supportFragmentManager)
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

    /**
     * ------------------------------------------------------------------------------------
     * 画面下のアイコン(FloatingActionButton)のアイコン切替処理
     * ここのアニメーションの部分はMainActivityから切り離したい
     * アニメーション管理クラスを作成するかも?
     * ------------------------------------------------------------------------------------
     */
    private fun switchFilterIcon(shapre : SharedPref, init : Boolean){
        /**
         * 設定値から読み出して表示切替
         */
        when (shapre.listActiveSwitch){
            /**
             * 全部表示
             */
            FrontConst.SharedPref.ALL_TODO_LIST.value ->{
                // all -> active
                if(init){
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_view_headline_white_24dp, null))
                } else {
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_grade_yellow_24dp, null))
                    shapre.listActiveSwitch = FrontConst.SharedPref.ACTIVE_TODO_LIST.value
                }
            }
            /**
             * Active表示
             */
            FrontConst.SharedPref.ACTIVE_TODO_LIST.value ->{
                // active -> inactive
                if(init){
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_grade_yellow_24dp, null))
                } else {
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_grade_gray_24dp, null))
                    shapre.listActiveSwitch = FrontConst.SharedPref.INACTIVE_TODO_LIST.value
                }
            }
            /**
             * Inactive表示
             */
            FrontConst.SharedPref.INACTIVE_TODO_LIST.value ->{
                // inactive -> all
                if(init){
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_grade_gray_24dp, null))
                } else {
                    fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_view_headline_white_24dp, null))
                    shapre.listActiveSwitch = FrontConst.SharedPref.ALL_TODO_LIST.value
                }
            }
        }
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
