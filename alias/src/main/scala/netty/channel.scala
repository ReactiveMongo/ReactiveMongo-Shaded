package reactivemongo.io.netty.channel

import _root_.io.netty.{ channel => c }

object `package` {
  type Channel = c.Channel

  type ChannelFuture = c.ChannelFuture

  type ChannelFutureListener = c.ChannelFutureListener

  object ChannelFutureListener {
    @inline def CLOSE = c.ChannelFutureListener.CLOSE
  }

  type ChannelHandlerContext = c.ChannelHandlerContext

  type ChannelId = c.ChannelId

  type ChannelInboundHandlerAdapter = c.ChannelInboundHandlerAdapter

  type ChannelInitializer[C <: c.Channel] = c.ChannelInitializer[C]

  type ChannelOption[T] = c.ChannelOption[T]

  type ChannelOutboundHandlerAdapter = c.ChannelOutboundHandlerAdapter

  object ChannelOption {
    @inline def AUTO_READ = c.ChannelOption.AUTO_READ

    @inline def CONNECT_TIMEOUT_MILLIS = c.ChannelOption.CONNECT_TIMEOUT_MILLIS

    @inline def SO_KEEPALIVE = c.ChannelOption.SO_KEEPALIVE

    @inline def TCP_NODELAY = c.ChannelOption.TCP_NODELAY

    @inline def SO_REUSEADDR = c.ChannelOption.SO_REUSEADDR
  }

  type ChannelPromise = c.ChannelPromise

  trait ChannelDuplexHandler extends c.ChannelDuplexHandler

  type DefaultChannelId = c.DefaultChannelId

  object DefaultChannelId {
    @inline def newInstance() = c.DefaultChannelId.newInstance()
  }

  type EventLoopGroup = c.EventLoopGroup
}

package object group {
  type ChannelGroupFuture = c.group.ChannelGroupFuture

  type ChannelGroupFutureListener = c.group.ChannelGroupFutureListener

  type DefaultChannelGroup = c.group.DefaultChannelGroup
}

package object embedded {
  type EmbeddedChannel = c.embedded.EmbeddedChannel
}
