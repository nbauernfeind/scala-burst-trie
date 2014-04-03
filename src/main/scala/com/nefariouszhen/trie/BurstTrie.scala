package com.nefariouszhen.trie

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

trait BurstTrie[T] {
  def put(word: String, t: T): Unit
  def query(q: String): Iterator[T]
}

class BurstTrieSet (impl: BurstTrie[String]) {
  def put(word: String): Unit = impl.put(word, word)
  def query(q: String): Iterator[String] = impl.query(q)
}

object BurstTrie {
  val DEFAULT_BURST = 10000
  val DEFAULT_GROWTH = 2

  def newMap[T](burstFactor: Int = BurstTrie.DEFAULT_BURST, growthFactor: Int = BurstTrie.DEFAULT_GROWTH): BurstTrie[T] = {
    new BurstTrieImpl[T, Iterable[T]](burstFactor, growthFactor, (_, t) => Some(t), () => None)
  }

  def newMultiMap[T](burstFactor: Int = BurstTrie.DEFAULT_BURST, growthFactor: Int = BurstTrie.DEFAULT_GROWTH, allowDuplicateEntries: Boolean = true): BurstTrie[T] = {
    if (allowDuplicateEntries) {
      new BurstTrieImpl[T, ArrayBuffer[T]](burstFactor, growthFactor, (arr, t) => arr += t, () => ArrayBuffer[T]())
    } else {
      new BurstTrieImpl[T, mutable.HashSet[T]](burstFactor, growthFactor, (arr, t) => arr += t, () => mutable.HashSet[T]())
    }
  }

  def newSuffixMap[T](burstFactor: Int = BurstTrie.DEFAULT_BURST, growthFactor: Int = BurstTrie.DEFAULT_GROWTH, allowDuplicateEntries: Boolean = true): BurstTrie[T] = {
    if (allowDuplicateEntries) {
      new BurstTrieImpl[T, mutable.Buffer[T]](burstFactor, growthFactor, (arr, t) => arr += t, () => mutable.ArrayBuffer[T]() ) {
        override def put(word: String, t: T): Unit = {
          for (idx <- 0 until word.length) {
            val suffix = word.slice(idx, word.length)
            super.put(suffix, t)
          }
        }
      }
    } else {
      new BurstTrieImpl[T, mutable.HashSet[T]](burstFactor, growthFactor, (arr, t) => arr += t, () => mutable.HashSet[T]() ) {
        override def put(word: String, t: T): Unit = {
          for (idx <- 0 until word.length) {
            val suffix = word.slice(idx, word.length)
            super.put(suffix, t)
          }
        }
      }
    }
  }

  def newSet(burstFactor: Int = BurstTrie.DEFAULT_BURST, growthFactor: Int = BurstTrie.DEFAULT_GROWTH): BurstTrieSet = {
    new BurstTrieSet(newMap[String](burstFactor, growthFactor))
  }

  def newSuffixSet(burstFactor: Int = BurstTrie.DEFAULT_BURST, growthFactor: Int = BurstTrie.DEFAULT_GROWTH, allowDuplicateEntries: Boolean = true): BurstTrieSet = {
    new BurstTrieSet(newSuffixMap[String](burstFactor, growthFactor, allowDuplicateEntries))
  }
}
