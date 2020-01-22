package susu.com.todo.controller.action

class UserIO {
    // static領域
    companion object {
        // シングルトンインスタンスの宣言
        private var instance: UserIO = UserIO()
        // インスタンス取得
        fun getInstance() : UserIO {
            return instance
        }
    }


}