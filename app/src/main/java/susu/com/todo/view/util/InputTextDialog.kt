package susu.com.todo.view.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Dialogを表示させるためのカスタムクラス
 */
class InputTextDialog( mc: Context) {
    // テキスト入力用のダイアログ
    private val mDialog = MyDialog()
    init {
        mDialog.mEdit =  EditText(mc)
    }

    /**
     * タイトル
     */
    var dialogTitle : String
        get() {
            return mDialog.mTitle
        }
        set(value) {
            mDialog.mTitle = value
        }
    /**
     * メッセージ
     */
    var dialogMessage : String
        get() {
            return mDialog.mMsg
        }
        set(value) {
            mDialog.mMsg = value
        }
    /**
     * 入力データ
     */
    var dialogTextData : String
        get() {
            if ( null != mDialog.mEdit) {
                mDialog.mTextData = mDialog.mEdit?.text.toString()
            }
            return mDialog.mTextData
        }
        set(value) {
            mDialog.mTextData = value
        }
    /**
     * 外部からテキストボックスの操作をする
     */
    var editText : EditText?
        get() {
            return mDialog.mEdit
        }
        set(value) {
            mDialog.mEdit = value
        }
    /**
     * OKボタンを利用するのかフラグ
     * 標準クラスのOKボタン押下の挙動をする
     */
    var isOkButton : Boolean
        get() {
            return mDialog.isOkButton
        }
        set(value) {
            mDialog.isOkButton = value
        }
    /**
     * OK時の処理
     * 標準クラスのOKボタン押下の挙動をする
     * OK押下で強制的に閉じさせる
     */
    var onOkClickListener : DialogInterface.OnClickListener
        get() {
            // 実際には使用しない
            return DialogInterface.OnClickListener {_, _ -> }
        }
        set(value) {
            mDialog.isOkButton = true
            mDialog.isCustomOkButton = false    //明示的に記載(書かなくても初期値がfalseである)
            mDialog.onOkClickListener = value
        }
    /**
     * キャンセルボタンを利用するのかフラグ
     */
    var isCancelButton : Boolean
        get() {
            return mDialog.isCancelButton
        }
        set(value) {
            mDialog.isCancelButton = value
        }
    /**
     * キャンセル時の処理
     */
    var onCancelClickListener : DialogInterface.OnClickListener
        get() {
            // 実際には使用しない
            return DialogInterface.OnClickListener {_, _ -> }
        }
        set(value) {
            mDialog.isCancelButton = true
            mDialog.onCancelClickListener = value
        }

    /**
     * OK時の処理
     * 標準クラスから切り離して、カスタムされたOKボタン押下時の挙動
     * つまり、OK押下で任意に閉じさせる
     * 処理内容については、外部から注入させる
     */
    var onOkButtonClickListener : View.OnClickListener
        get() {
            // 実際には使用しない
            return View.OnClickListener {  }
        }
        set(value) {
            mDialog.isOkButton = true
            mDialog.isCustomOkButton = true
            mDialog.onOkButtonClickListener = value
        }

    // 外部からカスタムされたDialogFragmentを閉じさせるためのメソッド
    fun dialogDismiss(){
        mDialog.diagDismiss()
    }

    /**
     * ダイアログの表示
     */
    fun openDialog(manager: FragmentManager) {
        mDialog.show( manager,"dialog")
    }

    /**
     * カスタム化されたDialogFragmentクラス
     */
    class MyDialog : DialogFragment() {
        // パラメータ未設定時の基本状態
        var mTitle : String = ""
        var mMsg  : String = ""
        var mEdit : EditText? = null
        var mTextData : String = ""
        // 標準OKボタン
        var isOkButton : Boolean = false
        var onOkClickListener : DialogInterface.OnClickListener = DialogInterface.OnClickListener {_, _ -> }
        // 標準Cancelボタン
        var isCancelButton : Boolean = false
        var onCancelClickListener : DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }

        // カスタムOKボタン
        var onOkButtonClickListener : View.OnClickListener = View.OnClickListener {  }
        var isCustomOkButton : Boolean = false

        /**
         * カスタムDialog生成
         */
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // 実際は AlertDialog を使用
            val dialogBuilder = AlertDialog.Builder(activity!!)

            // タイトル
            if (mTitle.isNotEmpty()) {
                dialogBuilder.setTitle(mTitle)
            } else {
                dialogBuilder.setTitle("テキスト入力")
            }
            // 小見出し
            if (mMsg.isNotEmpty()) {
                dialogBuilder.setMessage(mMsg)
            } else {
                dialogBuilder.setMessage("TODO項目を入力してください")
            }
            // 入力ボックス
            mEdit?.setText( mTextData )
            mEdit?.inputType = InputType.TYPE_CLASS_TEXT    //改行防止
            dialogBuilder.setView(mEdit)
            // キャンセルボタン有無
            if (isCancelButton) {
                dialogBuilder.setNegativeButton(getString(android.R.string.cancel), onCancelClickListener)
            }
            //OKボタン有無
            if (isOkButton && isCustomOkButton) {
                // OKボタン押下時にValidationを設けるため、DialogクラスのOKボタンの閉じる処理を切り離すためにnullを入れた
                dialogBuilder.setPositiveButton(getString(android.R.string.ok), null)
                // OKボタンオブジェクト取得
                val malertDiag : AlertDialog = dialogBuilder.show()
                val okButton : Button = malertDiag.getButton(DialogInterface.BUTTON_POSITIVE)
                okButton.setOnClickListener(onOkButtonClickListener)
                return malertDiag
            } else if(isOkButton && !isCustomOkButton){
                // カスタムOKボタンを利用しない場合
                dialogBuilder.setPositiveButton(getString(android.R.string.ok), onOkClickListener)
            }
            return dialogBuilder.show()
        }

        // onPause でダイアログを閉じている
        override fun onPause() {
            super.onPause()
            dismiss()
        }

        fun diagDismiss(){
            dismiss()
        }
    }
}