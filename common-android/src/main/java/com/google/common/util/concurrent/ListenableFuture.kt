package com.google.common.util.concurrent

import java.util.concurrent.Future

/**
 * Stub implementation for ListenableFuture interface
 * This provides a compile-time reference that R8 can use
 */
interface ListenableFuture<V> : Future<V>