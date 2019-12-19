package reactivemongo.io.netty.channel.socket

import io.netty.channel.{ socket => s }

package object nio {
  type NioSocketChannel = s.nio.NioSocketChannel

  type NioServerSocketChannel = s.nio.NioServerSocketChannel
}
