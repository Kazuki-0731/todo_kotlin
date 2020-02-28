package susu.com.todo.controller.action

import android.animation.Animator
import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import susu.com.todo.R
import susu.com.todo.mdoel.database.DBHelper
import susu.com.todo.mdoel.dictionary.SharedPref
import susu.com.todo.mdoel.entities.DataModel
import susu.com.todo.view.animation.Floating
import susu.com.todo.view.common.FrontConst
import susu.com.todo.view.fragment.TodoFragment
import susu.com.todo.view.util.InputTextDialog

/**
 * Controller側は殆どリスナーの処理を記述する
 */
class UserIO (activity: Activity, fragmentManager: FragmentManager) {
    // アニメーションプロパティ
    private lateinit var floating : Floating
    private lateinit var openingAnimation: Animator
    private lateinit var closingAnimation: Animator

    // static領域
    companion object {
        lateinit var statefulActivity : Activity
        lateinit var statefulFragment : FragmentManager
    }

    init {
        statefulActivity = activity
        statefulFragment = fragmentManager

        // 設定値から読み出して初期表示
        switchFilterIcon(SharedPref.getInstance(), true)

        // アニメーションの状態初期化
        Floating.state = Floating.FloatingActionState.NORMAL

        // リスナーのセット
        setButtonListener()
    }

    /**
     * ボタンのonClickのリスナー群
     */
    private fun setButtonListener(){
        // appContextを保持
        floating = Floating(statefulActivity)

        // アニメーションオブジェクト生成
        openingAnimation = floating.createOpenFloatingActionButton()
        closingAnimation = floating.createCloseFloatingActionButton()

        // Active/Inactive切替ボタン押下処理
        statefulActivity.fab_active_inactive.setOnClickListener {
            // 設定値から読み出して表示切替
            switchFilterIcon(SharedPref.getInstance(), false)
            // ListViewリロード
            TodoFragment.getInstance().reload(statefulActivity, DBHelper.getInstance!!)
        }

        // 右下のTODO追加ボタン押下処理
        statefulActivity.fab_add.setOnClickListener { view ->
            // 回転
            if (Floating.state == Floating.FloatingActionState.NORMAL && !openingAnimation.isRunning) {
                openFloatingActionButton()
            }
            Log.d("debug", "Floating.openingAnimation.isRunning : ".plus(openingAnimation.isRunning))
            Log.d("debug", "FloatingFloating.state : ".plus(Floating.state))

            // ダイアログ生成
            val dialog = InputTextDialog(statefulActivity)
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
                if(DBHelper.getInstance!!.getMaxID() >= FrontConst.Limit.LISTVIEW_REGISTER_LIMIT.value){
                    // ダイアログを閉じないで新規ダイアログ表示
                    val warningDialog = InputTextDialog(statefulActivity)
                    warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                    warningDialog.dialogMessage = "最大登録件数(" +
                            FrontConst.Limit.LISTVIEW_REGISTER_LIMIT.value +
                            ")を超えるため、１件以上削除してください"
                    warningDialog.editText = null
                    warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                    warningDialog.isOkButton = true
                    warningDialog.isCancelButton = false
                    // ダイアログ表示
                    warningDialog.openDialog(statefulFragment)
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
                        val warningDialog = InputTextDialog(statefulActivity)
                        warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                        warningDialog.dialogMessage = "最大文字数を超えるため、" +
                                FrontConst.Limit.UPPER_LIMIT_OF_INPUT_VALUE.value +
                                "文字以下で入力してください"
                        warningDialog.editText = null
                        warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                        warningDialog.isOkButton = true
                        warningDialog.isCancelButton = false
                        // ダイアログ表示
                        warningDialog.openDialog(statefulFragment)
                    } else if(textData.isEmpty()){
                        // ダイアログを閉じないで新規ダイアログ表示
                        val warningDialog = InputTextDialog(statefulActivity)
                        warningDialog.dialogTitle = "⚠️ 警告 ⚠️"
                        warningDialog.dialogMessage = "1文字以上で入力してください"
                        warningDialog.editText = null
                        warningDialog.onOkClickListener = DialogInterface.OnClickListener { _, _->}
                        warningDialog.isOkButton = true
                        warningDialog.isCancelButton = false
                        // ダイアログ表示
                        warningDialog.openDialog(statefulFragment)
                    } else {
                        // DBに保存
                        val todoRecord = DataModel(
                            DBHelper.getInstance!!.getMaxID(),
                            textData,
                            FrontConst.Init.INITIAL_VALUE_OF_STATUS_WHEN_TODO_IS_ADDED.value // 初期値をInactive(0)とする
                        )
                        // 挿入
                        DBHelper.getInstance!!.insertTODO(todoRecord)
                        // 保存
                        Snackbar.make(view,"$textData をTODOリストへ追加しました", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        // ListViewリロード
                        TodoFragment.getInstance().reload(statefulActivity, DBHelper.getInstance!!)
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
            dialog.openDialog(statefulFragment)
        }
    }


    /**
     * ------------------------------------------------------------------------------------
     * 画面下のアイコン(FloatingActionButton)のアイコン切替処理
     * ここのアニメーションの部分はMainActivityから切り離したい
     * アニメーション管理クラスを作成するかも?
     * ------------------------------------------------------------------------------------
     */
    private fun switchFilterIcon(shapre : SharedPref?, init : Boolean){
        /**
         * 設定値から読み出して表示切替
         */
        when (shapre!!.listActiveSwitch){
            /**
             * 全部表示
             */
            FrontConst.SharedPref.ALL_TODO_LIST.value ->{
                // all -> active
                if(init){
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_view_headline_white_24dp, null))
                } else {
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_grade_yellow_24dp, null))
                    shapre.listActiveSwitch = FrontConst.SharedPref.ACTIVE_TODO_LIST.value
                }
            }
            /**
             * Active表示
             */
            FrontConst.SharedPref.ACTIVE_TODO_LIST.value ->{
                // active -> inactive
                if(init){
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_grade_yellow_24dp, null))
                } else {
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_grade_gray_24dp, null))
                    shapre.listActiveSwitch = FrontConst.SharedPref.INACTIVE_TODO_LIST.value
                }
            }
            /**
             * Inactive表示
             */
            FrontConst.SharedPref.INACTIVE_TODO_LIST.value ->{
                // inactive -> all
                if(init){
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_grade_gray_24dp, null))
                } else {
                    statefulActivity.fab_active_inactive.setImageDrawable(ResourcesCompat.getDrawable(statefulActivity.resources, R.drawable.ic_view_headline_white_24dp, null))
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