package reactivemongo.io.netty.util

import _root_.io.netty.{ util => u }

object `package` {
  type AttributeKey[T] = u.AttributeKey[T]

  object AttributeKey {
    @inline def newInstance[T](k: String) = u.AttributeKey.newInstance[T](k)
  }

  object Version {
    @inline def identify = u.Version.identify
  }
}

object concurrent {
  object GlobalEventExecutor {
    @inline def INSTANCE = u.concurrent.GlobalEventExecutor.INSTANCE
  }

  type Future[T] = u.concurrent.Future[T]

  type GenericFutureListener[F <: Future[_]] = u.concurrent.GenericFutureListener[F]
}
