package susu.com.todo.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
     * OK時の処理
     */
    var onOkClickListener : DialogInterface.OnClickListener
        get() {
            // 実際には使用しない
            return DialogInterface.OnClickListener {_, _ -> }
        }
        set(value) {
            mDialog.isOkButton = true
            mDialog.onOkClickListener = value
        }
    /**
     * キャンセルボタンを利用するのか
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
     * ダイアログの表示
     */
    fun openDialog(manager: FragmentManager) {
        mDialog.show( manager,"dialog")
    }

    /**
     * DialogFragment子クラス
     */
    class MyDialog : DialogFragment() {
        // パラメータ未設定時の基本状態
        var mTitle : String = ""
        var mMsg  : String = ""
        var mEdit : EditText? = null
        var mTextData : String = ""
        var isOkButton : Boolean = false
        var onOkClickListener      : DialogInterface.OnClickListener = DialogInterface.OnClickListener {_, _ -> }
        var isCancelButton : Boolean = false
        var onCancelClickListener : DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }

        /**
         * Dialog生成
         */
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // 実際は AlertDialog を使用
            val dialogBuilder = AlertDialog.Builder(activity!!)

            // タイトル
            if (mTitle.isNotEmpty()) {
                dialogBuilder.setTitle(mTitle)
            } else {
                dialogBuilder.setTitle("TODO入力")
            }
            // 小見出し
            if (mMsg.isNotEmpty()) {
                dialogBuilder.setMessage(mMsg)
            }
            // 入力ボックス
            mEdit?.setText( mTextData )
            dialogBuilder.setView(mEdit)
            //OKボタン有無
            if (isOkButton) {
                dialogBuilder.setPositiveButton(getString(android.R.string.ok), onOkClickListener)
            }
            // キャンセルボタン有無
            if (isCancelButton) {
                dialogBuilder.setNegativeButton(getString(android.R.string.cancel), onCancelClickListener)
            }
            return dialogBuilder.create()
        }
        // onPause でダイアログを閉じている
        override fun onPause() {
            super.onPause()
            dismiss()
        }
    }
}