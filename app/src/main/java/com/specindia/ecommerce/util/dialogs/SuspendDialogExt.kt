package com.specindia.ecommerce.util.dialogs

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Shows the [DialogFragment] using [fragmentManager] and [tag]
 *
 * This function suspends till [DialogFragment] is destroyed
 */
suspend fun DialogFragment.showAwait(
    fragmentManager: FragmentManager,
    tag: String? = null
) = suspendCancellableCoroutine<Unit> {
    val observer = DialogLifeCycleObserver {
        lifecycle.removeObserver(this)

        it.resume(Unit)
    }

    lifecycle.addObserver(observer)

    show(fragmentManager, tag)
}

/**
 * Shows the [DialogFragment] using [fragmentManager] and [tag]
 *
 * This function suspends till [DialogFragment] is destroyed
 *
 * @return the value stored on [SuspendDialogResult.result]
 */
suspend fun <T> SuspendDialogResult<T>.showAwaitResult(
    fragmentManager: FragmentManager,
    tag: String? = null
) = suspendCancellableCoroutine<T?> {
    if (this !is DialogFragment) {
        throw IllegalArgumentException("Only ${DialogFragment::class} supported with ${SuspendDialogResult::class}")
    }

    val observer = DialogLifeCycleObserver {
        lifecycle.removeObserver(this)

        it.resume(this@showAwaitResult.result)
    }

    lifecycle.addObserver(observer)

    show(fragmentManager, tag)
}

private class DialogLifeCycleObserver(
    private val onDestroy: LifecycleObserver.() -> Unit
) : DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        onDestroy()
    }
}