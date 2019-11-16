package susu.com.todo

import com.facebook.stetho.Stetho

/**
 * Debug ビルド時に使用されるApplication クラス
 *
 * ※debug/AndroidManifest.xml でこのクラスに置き換える設定をしてあります
 */
class DebugApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()

        // Stetho の設定
        // https://facebook.github.io/stetho/
        Stetho.newInitializerBuilder(applicationContext)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(applicationContext))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
            .build()
            .also { builder -> Stetho.initialize(builder) }
    }
}
