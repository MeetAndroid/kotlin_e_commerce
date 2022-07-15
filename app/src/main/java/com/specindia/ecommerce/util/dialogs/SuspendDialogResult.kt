package com.specindia.ecommerce.util.dialogs

/**
 * Provides [SuspendDialogResult.result] member to return from the [DialogFragment]
 */
interface SuspendDialogResult<T> {
    var result: T?
}