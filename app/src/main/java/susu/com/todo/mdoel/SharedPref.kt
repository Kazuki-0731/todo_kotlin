package susu.com.todo.mdoel

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * sharedPreferencesの拡張クラス
 *
 * 初期値があるプロパティ、Nullableなプロパティに対して、
 * 自動で保存する機能を継承
 */
class SharedPref(context: Context) {

    private val appPref: SharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    // hoge や todoListItemへの読み書きがそのまま SharedPreferences への読み書きになる
//    var hoge: Int by pref(default = 0)
    var todoListItem: String? by nullablePref()

    /**
     * デフォルト値があるプロパティに対してgetter/setterでのsharedPreで保存/読み取り
     */
    private fun <T : Any> pref(default: T) = object : ReadWriteProperty<SharedPref, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: SharedPref, property: KProperty<*>): T {
            val key = property.name
            return (appPref.all[key] as? T) ?: run {
                put(key, default)
                default
            }
        }

        override fun setValue(thisRef: SharedPref, property: KProperty<*>, value: T) {
            val key = property.name
            put(key, value)
        }
    }

    /**
     * nullableがあるプロパティに対してgetter/setterでのsharedPreで保存/読み取り
     */
    private fun <T : Any?> nullablePref() = object : ReadWriteProperty<SharedPref, T?> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: SharedPref, property: KProperty<*>): T? {
            val key = property.name
            return appPref.all[key] as? T?
        }

        override fun setValue(thisRef: SharedPref, property: KProperty<*>, value: T?) {
            val key = property.name
            put(key, value)
        }
    }

    /**
     * sharedPrefで保存
     */
    private fun <T : Any?> put(key: String, value: T?) {
        val editor = appPref.edit()
        when (value) {
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Set<*> -> editor.putStringSet(key, value.map { it as String }.toSet())
            null -> editor.remove(key)
            else -> throw IllegalArgumentException("用意されていない型")
        }
        editor.apply()
    }
}