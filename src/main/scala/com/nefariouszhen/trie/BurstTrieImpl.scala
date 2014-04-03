package com.nefariouszhen.trie

import java.util
import scala.collection.JavaConversions._

class BurstTrieImpl[T, S <: Iterable[T]] (burstFactor: Int, growthFactor: Int, accumulateHit: (S, T) => S, emptyHits: () => S) extends BurstTrie[T] {
  require(burstFactor >= 0, "The burstFactor must be a positive integer.")
  require(growthFactor > 0, "The growthFactor must be larger than 0.")
  require(accumulateHit != null, "An accumulation method must be supplied to store values.")
  require(emptyHits != null, "A default hit container must be supplied.")

  private[this] val root = new AccessNode

  def put(word: String, t: T): Unit = root.put(word, t)

  def query(q: String): Iterator[T] = root.query(q)

  private trait Node {
    protected[BurstTrieImpl] var hits: S = emptyHits()

    def put(word: String, t: T): Unit
    def query(q: String): Iterator[T]
  }

  private class AccessNode(len: Int = 1) extends Node {
    private[this] val children = new util.TreeMap[String, Node]()

    def put(word: String, t: T): Unit = {
      if (word.isEmpty) {
        hits = accumulateHit(hits, t)
      } else {
        val idx = word.slice(0, len)
        val tail = word.slice(len, word.length)
        val child = getOrCreateChild(idx)
        child.put(tail, t)
        child match {
          case container: ContainerNode if container.size > burstFactor => children(idx) = container.burst()
          case _ =>
        }
      }
    }

    def query(q: String): Iterator[T] = new AccessIterator(q)

    class AccessIterator (q: String) extends Iterator[T] {
      private[this] val prefix = q.slice(0, len)
      private[this] val tail = q.slice(len, q.length)

      private[this] val hitIterator = if (q.isEmpty) hits.iterator else Iterator[T]()
      private[this] val childIterator = children.tailMap(prefix).iterator
        .takeWhile({ case (key, _) => key.startsWith(prefix) })
        .flatMap({ case (_, child) => child.query(tail) })

      def hasNext: Boolean = hitIterator.hasNext || childIterator.hasNext
      def next(): T = if (hitIterator.hasNext) hitIterator.next() else childIterator.next()
    }

    private[this] def getOrCreateChild(idx: String): Node = {
      children.getOrElseUpdate(idx, new ContainerNode(len * growthFactor))
    }
  }

  private class ContainerNode(len: Int = 1) extends Node {
    private[this] val children = new util.TreeMap[String, S]()

    def size = children.size

    def put(word: String, t: T): Unit = {
      if (word.isEmpty) {
        hits = accumulateHit(hits, t)
      } else {
        children.put(word, accumulateHit(getOrCreateChild(word), t))
      }
    }

    def burst(): Node = {
      val ret = new AccessNode(len = len)
      for ((key, childIterator) <- children; child <- childIterator) {
        ret.put(key, child)
      }
      ret.hits = hits
      ret
    }

    def query(q: String): Iterator[T] = new ContainerIterator(q)

    class ContainerIterator (q: String) extends Iterator[T] {
      private[this] val hitIterator = if (q.isEmpty) hits.iterator else Iterator[T]()
      private[this] val childIterator = children.tailMap(q).iterator
        .takeWhile({ case (key, _) => key.startsWith(q) })
        .flatMap({ case (_, child) => child.iterator })

      def hasNext: Boolean = hitIterator.hasNext || childIterator.hasNext
      def next(): T = if (hitIterator.hasNext) hitIterator.next() else childIterator.next()
    }

    def getOrCreateChild(word: String): S = {
      children.get(word) match {
        case null => emptyHits()
        case child => child
      }
    }
  }
}
