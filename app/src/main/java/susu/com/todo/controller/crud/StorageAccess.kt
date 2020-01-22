package susu.com.todo.controller.crud

// シングルトンクラス
class StorageAccess {
    // static領域
    companion object {
        // シングルトンインスタンスの宣言
        private var instance: StorageAccess = StorageAccess()
        // インスタンス取得
        fun getInstance() : StorageAccess {
            return instance
        }
    }



}
