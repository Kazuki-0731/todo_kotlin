package susu.com.todo.view.animation

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import susu.com.todo.R

class Floating(val context: Context?){
    // アニメーション定数
    enum class FloatingActionState {
        NORMAL,
        ANIMATED
    }

    var state: FloatingActionState = FloatingActionState.NORMAL

    companion object {
        private val INSTANCE = Floating(context = null)
        fun getInstance(): Floating {
            return INSTANCE
        }
    }

    /**
     * ------------------------------------------------------------------------------------
     * 右下のアイコン(FloatingActionButton)のアニメーション処理
     * ここのアニメーションの部分はMainActivityから切り離したい
     * アニメーション管理クラスを作成するかも?
     * ------------------------------------------------------------------------------------
     */
    // 開く動作オブジェクト
    fun createOpenFloatingActionButton(): Animator {
        val anim = AnimatorInflater.loadAnimator(context, R.animator.fab_open).apply {
            setTarget(R.id.fab_add)
        }
        anim.interpolator = DecelerateInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                state = FloatingActionState.ANIMATED
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.end()
                state = FloatingActionState.ANIMATED
            }
        })
        return anim
    }

    // 閉じる動作オブジェクト
    fun createCloseFloatingActionButton(): Animator {
        val anim = AnimatorInflater.loadAnimator(context, R.animator.fab_close)
        anim.setTarget(R.id.fab_add)
        anim.interpolator = AccelerateInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                state = FloatingActionState.NORMAL
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.end()
                state = FloatingActionState.NORMAL
            }
        })
        return anim
    }

}