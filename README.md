# Scala Burst Trie

This is an implementation of [Burst Tries](http://www.cs.mu.oz.au/~jz/fulltext/acmtois02.pdf). It is enhanced to also
take advantage of techniques that are used in GWT's implementation of
[PrefixTree](https://code.google.com/p/google-web-toolkit/source/browse/trunk/user/src/com/google/gwt/user/client/ui/PrefixTree.java).
I used this implementation on Stripe's CTF 3, level 3 in my fastest multi-host solution.

## Maven Setup

```xml
<dependency>
  <groupId>com.nefariouszhen.trie</groupId>
  <artifactId>scala-burst-trie_${scala.binary.version}</artifactId>
  <version>0.2</version>
</dependency>
```

Note: This artifact is cross compiled against multiple versions of scala and follows the latest scala-version naming conventions.

## Getting Started

```scala
def indexContent(getKey: T => String, content: Iterable[T]): BurstTrie[T] = {
  val trie = BurstTrie.newMap[T]()
  content.foreach(c => trie.put(getKey(c), c))
  trie
}

def queryContent(prefixToFind: String, trie: BurstTrie[T]) {
    trie.query(prefixToFind).foreach(println)
}
```

You may also be interested in the implementation parameterizations `newMultiMap[T]`, `newSuffixMap[T]`, `newSet`, and `newSuffixSet`.

## Parameterization

1. The `burstFactor` (`default = 10000`, `require >= 0`) is the number of entries to store in a container node (i.e. leaf) before converting
the container node into an access node (by splitting up the container into many different containers). Note that a
traditional trie has a burstFactor of 0 (i.e. no container nodes)!

2. The `growthFactor` (`default = 2`, `require > 0`) is how quickly the length of the prefix chunk grows. At every depth of the tree,
there is an explicit depth of how many characters to strip off of the key and use as a key-slice into the local node's
internal structure(s). GWT's implementation is a fixed factor of 2. A traditional trie has a fixed factor of 1.

3. The `allowDuplicateKeys` (`default = true`), when using the `multiMap` variants, determines whether or not for the exact same (key, value)
pair whether or not the map will store an additional entry. This defaults to true because of the performance hit that is
taken when it is off (i.e. On internal nodes, instead of using an Array to store each value, to prevent duplicates
it would instead use a HashSet). Turning this feature off is, clearly, much more costly than delegating duplicate prevention
to the indexing caller.

## Suffix Trees

```scala
val suffixMap = BurstTrie.newSuffixMap[T]()
```

Suffix maps allow you to find all of your content by any substring very efficiently. However, the traditional use case,
indexing word placement in documents, needs to be implemented with care. Specifically, don't make a map from key to position. Instead,
make a map from key to an object that represents that word and all of its positions. This way, all of the positions are
listed once, instead of once for every suffix (i.e. it reduces memory needs by a factor of `key.length`).

## Thread-Safety Warning

Don't use multithreaded writers, and don't read while you're writing. The code is currently not safe for such operations. If
you'd be interested in a thread-safe implementation of this, please let me know and I can try to work something out.
