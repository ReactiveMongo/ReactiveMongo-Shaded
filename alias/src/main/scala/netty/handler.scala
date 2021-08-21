package reactivemongo.io.netty.handler

import _root_.io.netty.{ buffer => b }
import _root_.io.netty.handler.{ codec => c, ssl => s, timeout => t }

package object codec {
  type ByteToMessageDecoder = c.ByteToMessageDecoder

  type MessageToMessageDecoder[T] = c.MessageToMessageDecoder[T]

  type MessageToByteEncoder[T] = c.MessageToByteEncoder[T]

  object compression {
    type Snappy = c.compression.Snappy
  }
}

package object timeout {
  type IdleStateEvent = t.IdleStateEvent

  type IdleStateHandler = t.IdleStateHandler
}

package object ssl {
  type SslHandler = s.SslHandler
}
