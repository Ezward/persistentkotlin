package com.lumpofcode.collection.sparsevector

/**
 * Created by emurphy on 7/10/17.
 */
interface Vector<T>
{
    fun isEmpty(): Boolean
    {
        return 0 == size()
    }

    fun size(): Int
    fun get(index: Int): T
    fun set(index: Int, value: T): Vector<T>
    fun clear(index: Int): Vector<T>
}