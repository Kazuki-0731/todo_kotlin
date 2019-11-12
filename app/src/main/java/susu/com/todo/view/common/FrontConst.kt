package susu.com.todo.view.common

object FrontConst {
    // 上限を管理する定数クラス
    enum class Limit(val value : Int) {
        /**
         * ListViewに登録できる最大値
         */
        LISTVIEW_REGISTER_LIMIT(100),
        /**
         * テキストボックスの入力値の上限
         */
        UPPER_LIMIT_OF_INPUT_VALUE(50)
    }

    // DBのSTATUSの初期値
    enum class Init(val value : Int) {
        /**
         * TODO追加時のSTATUSの初期値
         */
        INITIAL_VALUE_OF_STATUS_WHEN_TODO_IS_ADDED(0)
    }

    // sharedPrefで利用する定数
    enum class SharedPref(val value : String) {
        /**
         * SharedPreferences.listActiveSwitchの値
         * すべて表示することを意味する
         */
        ALL_TODO_LIST("0"),

        /**
         * SharedPreferences.listActiveSwitchの値
         * Activeのみ表示することを意味する
         */
        ACTIVE_TODO_LIST("1"),

        /**
         * SharedPreferences.listActiveSwitchの値
         * Activeのみ表示することを意味する
         */
        INACTIVE_TODO_LIST("2")
    }
}
