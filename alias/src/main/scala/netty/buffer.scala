package reactivemongo.io.netty

import _root_.io.netty.{ buffer => b }

package object buffer {
  type ByteBuf = b.ByteBuf

  object Unpooled {
    @inline def buffer(capacity: Int) = b.Unpooled.buffer(capacity)

    @inline def buffer(initialCapacity: Int, maxCapacity: Int) =
      b.Unpooled.buffer(initialCapacity, maxCapacity)

    @inline def copiedBuffer(bytes: Array[Byte]) =
      b.Unpooled.copiedBuffer(bytes)

    @inline def wrappedBuffer(orig: java.nio.ByteBuffer) =
      b.Unpooled.wrappedBuffer(orig)

    @inline def wrappedBuffer(bytes: Array[Byte]) =
      b.Unpooled.wrappedBuffer(bytes)

    @inline def wrappedBuffer(bytes: b.ByteBuf*) =
      b.Unpooled.wrappedBuffer(bytes: _*)

    @inline def EMPTY_BUFFER = b.Unpooled.EMPTY_BUFFER
  }
}
