package susu.com.todo

/**
 * Debug ビルド時に使用されるApplication クラス
 *
 * ※debug/AndroidManifest.xml でこのクラスに置き換える設定をしてあります
 */
class DebugApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()
    }
}
