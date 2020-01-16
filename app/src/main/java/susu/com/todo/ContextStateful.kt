package susu.com.todo

import androidx.appcompat.app.AppCompatActivity

// シングルトンなContextカスタムクラス
class ContextStateful {
    // static領域
    companion object {
        // 遅延宣言
        private lateinit var instance: AppCompatActivity
        // Application#onCreateのタイミングでシングルトンが生成される
        fun onCreateApplication(applicationContext: AppCompatActivity) {
            instance = applicationContext
        }
        // シングルトンなインスタンス取得
        fun getInstance(): AppCompatActivity {
            return instance
        }
    }
}
