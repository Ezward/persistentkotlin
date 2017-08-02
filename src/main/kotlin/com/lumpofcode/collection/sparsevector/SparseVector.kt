package com.lumpofcode.collection.sparsevector

import com.lumpofcode.math.IntegerMath

private const val NODE_SIZE = 4
private const val NODE_BITS = 0xFF


object SparseVectors
{
    /**
     * Factory method to create a sparse vector.
     * All elements start out with the value zero.
     *
     * @param size the size (number of elements in the vector)
     * @param zero the value for 'missing' elements
     * @return a vector of the given size with all zero elements
     */
    fun <T> ofSize(size: Int, zero: T): Vector<T>
    {
        if (size < 0) throw IndexOutOfBoundsException();

        if (size <= NODE_SIZE)
        {
            //
            // small vector does not require a tree
            //
            return SparseVector0(size, zero)
        }

        //
        // requires a tree, create the empty root node
        //
        return SparseVectorTrie0(depth(size), size, zero)
    }

    internal fun <T> ofSize(level: Int, size: Int, zero: T): Vector<T>
    {
        if (level < 0) throw IndexOutOfBoundsException();
        if (size < 0) throw IndexOutOfBoundsException();

        if (0 == level)
        {
            //
            // small vector does not require a tree
            //
            return SparseVector0(size, zero)
        }

        //
        // requires a tree, create the empty root node
        //
        return SparseVectorTrie0(level, size, zero)
    }

}

class SparseVector0<T>(val size: Int, val zero: T) : Vector<T>
{
    override fun isEmpty() = (0 == size);

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        return SparseVector1(size, zero, 1 shl index, value)
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // already empty, so we can return this
        //
        return this;
    }
}

class SparseVector1<T>(val size: Int, val zero: T, val sparsity: Int, val e0: T) : Vector<T>
{
    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> e0
            else -> zero
        }
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        when (i)
        {
            0 -> return SparseVector1(size, zero, sparsity, value)
        }

        //
        // index corresponds to a zero element, so we are adding a new non-zero element
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val sparsity4 = sparsity or (1 shl index)
        val j = sparseIndex(sparsity4, index)
        when (j)
        {
            0 -> return SparseVector2(size, zero, sparsity4, value, e0)
        }
        return SparseVector2(size, zero, sparsity4, e0, value)
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 ->  SparseVector0(size, zero)
            else -> this
        }

    }
}

class SparseVector2<T>(val size: Int, val zero: T, val sparsity: Int, val e0: T, val e1: T) : Vector<T>
{
    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> e0
            1 -> e1
            else -> zero
        }
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        when (i)
        {
            0 -> return SparseVector2(size, zero, sparsity, value, e1)
            1 -> return SparseVector2(size, zero, sparsity, e0, value)
        }

        //
        // index corresponds to a zero element, so we are adding a new non-zero element
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val sparsity4 = sparsity or (1 shl index)
        val j = sparseIndex(sparsity4, index)
        when (j)
        {
            0 -> return SparseVector3(size, zero, sparsity4, value, e0, e1)
            1 -> return SparseVector3(size, zero, sparsity4, e0, value, e1)
        }
        return SparseVector3(size, zero, sparsity4, e0, e1, value)
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> SparseVector1(size, zero, sparsity and (1 shl index).inv(), e1)
            1 -> SparseVector1(size, zero, sparsity and (1 shl index).inv(), e0)
            else -> this
        }

    }

}

class SparseVector3<T>(val size: Int, val zero: T, val sparsity: Int, val e0: T, val e1: T, val e2: T) : Vector<T>
{
    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> e0
            1 -> e1
            2 -> e2
            else -> zero
        }
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        when (i)
        {
            0 -> return SparseVector3(size, zero, sparsity, value, e1, e2)
            1 -> return SparseVector3(size, zero, sparsity, e0, value, e2)
            2 -> return SparseVector3(size, zero, sparsity, e0, e1, value)
        }

        //
        // index corresponds to a zero element, so we are adding a new non-zero element
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val sparsity4 = sparsity or (1 shl index)
        val j = sparseIndex(sparsity4, index)
        when (j)
        {
            0 -> return SparseVector4(size, zero, sparsity4, value, e0, e1, e2)
            1 -> return SparseVector4(size, zero, sparsity4, e0, value, e1, e2)
            2 -> return SparseVector4(size, zero, sparsity4, e0, e1, value, e2)
        }
        return return SparseVector4(size, zero, sparsity4, e0, e1, e2, value)
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> SparseVector2(size, zero, sparsity and (1 shl index).inv(), e1, e2)
            1 -> SparseVector2(size, zero, sparsity and (1 shl index).inv(), e0, e2)
            2 -> SparseVector2(size, zero, sparsity and (1 shl index).inv(), e0, e1)
            else -> this
        }

    }

}

class SparseVector4<T>(val size: Int, val zero: T, val sparsity: Int, val e0: T, val e1: T, val e2: T, val e3: T) : Vector<T>
{
    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> e0
            1 -> e1
            2 -> e2
            3 -> e3
            else -> zero
        }
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        when (i)
        {
            0 -> return SparseVector4(size, zero, sparsity, value, e1, e2, e3)
            1 -> return SparseVector4(size, zero, sparsity, e0, value, e2, e3)
            2 -> return SparseVector4(size, zero, sparsity, e0, e1, value, e3)
            3 -> return SparseVector4(size, zero, sparsity, e0, e1, e2, value)
        }

        //
        // index corresponds to a zero element, so we are adding a new non-zero element
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val sparsity4 = sparsity or (1 shl index)
        val j = sparseIndex(sparsity4, index)
        when (j)
        {
            0 -> return SparseVector4(size, zero, sparsity4, value, e0, e1, e2)
            1 -> return SparseVector4(size, zero, sparsity4, e0, value, e1, e2)
            2 -> return SparseVector4(size, zero, sparsity4, e0, e1, value, e2)
            3 -> return SparseVector4(size, zero, sparsity4, e0, e1, e2, value)
        }

        //
        // We should not get here, this should be caught at the top
        //
        throw IndexOutOfBoundsException();
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val i = sparseIndex(sparsity, index)
        return when (i)
        {
            0 -> SparseVector3(size, zero, sparsity and (1 shl index).inv(), e1, e2, e3)
            1 -> SparseVector3(size, zero, sparsity and (1 shl index).inv(), e0, e2, e3)
            2 -> SparseVector3(size, zero, sparsity and (1 shl index).inv(), e0, e1, e3)
            3 -> SparseVector3(size, zero, sparsity and (1 shl index).inv(), e0, e1, e2)
            else -> this
        }

    }
}

class SparseVectorTrie0<T>(val level: Int, val size: Int, val zero: T) : Vector<T>
{
    val childSize = IntegerMath.power(NODE_SIZE, level)

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        val trieIndex = index / childSize
        val trieSparsity = 1 shl trieIndex // sparsity at this trie level
        val sizeOfChild = if(index < ((size / childSize) * childSize)) childSize else (size % childSize)
        val indexInChild = index % childSize

        //
        // add at index zero
        //
        return SparseVectorTrie1(level, size, zero, trieSparsity, SparseVectors.ofSize(level - 1, sizeOfChild, zero).set(indexInChild, value))
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // this is already empty, so return this
        //
        return this
    }

}

class SparseVectorTrie1<T>(val level: Int, val size: Int, val zero: T, val sparsity: Int, val e0: Vector<T>) : Vector<T>
{
    val load = 1;

    val childSize = IntegerMath.power(NODE_SIZE, level)

    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index / childSize)
        when (i)
        {
            0 -> return e0.get(index % childSize)
        }

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        when (i)
        {
        // update the sparse element at the given index
            0 -> return SparseVectorTrie1(level, size, zero, sparsity, e0.set(index % childSize, value))
        }

        //
        // index corresponds to a zero element in this trie, so we are adding a new non-zero element (a new child vector)
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val trieSparsity = sparsity or (1 shl trieIndex) // sparsity at this trie level
        val j = sparseIndex(trieSparsity, trieIndex)
        when (j)
        {
            0 -> return SparseVectorTrie2(level, size, zero, trieSparsity, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e0)
        }

        //
        // add at index 1
        //
        val sizeOfChild = if(index < ((size / childSize) * childSize)) childSize else (size % childSize)
        val indexInChild = index % childSize
        return SparseVectorTrie2(level, size, zero, trieSparsity, e0, SparseVectors.ofSize(level - 1, sizeOfChild, zero).set(indexInChild, value))
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // short circuit if the value we intend to clear is already zero
        //
        return if(zero == this.get(index)) this else innerClear(index)
    }


    /**
     * internal implementation of clear that
     * presumes the index is in bounds
     * and the value to be cleared is non-zero
     *
     * @param index must be in bounds and reference and non-zero value
     * @return a new vector with the value cleared to zero
     */
    internal fun innerClear(index: Int): Vector<T>
    {
        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        return when (i)
        {
            0 ->  SparseVectorTrie0(level, size, zero)
            else -> this
        }
    }
}

class SparseVectorTrie2<T>(val level: Int, val size: Int, val zero: T, val sparsity: Int, val e0: Vector<T>, val e1: Vector<T>) : Vector<T>
{
    val childSize = IntegerMath.power(NODE_SIZE, level)

    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val i = sparseIndex(sparsity, index / childSize)
        when (i)
        {
            0 -> return e0.get(index % childSize)
            1 -> return e1.get(index % childSize)
        }

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        when (i)
        {
        // update the sparse element at the given index
            0 -> return SparseVectorTrie2(level, size, zero, sparsity, e0.set(index % childSize, value), e1)
            1 -> return SparseVectorTrie2(level, size, zero, sparsity, e0, e1.set(index % childSize, value))
        }

        //
        // index corresponds to a zero element in this trie, so we are adding a new non-zero element (a new child vector)
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val trieSparsity = sparsity or (1 shl trieIndex) // sparsity at this trie level
        val j = sparseIndex(trieSparsity, trieIndex)
        when (j)
        {
            0 -> return SparseVectorTrie3(level, size, zero, trieSparsity, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e0, e1)
            1 -> return SparseVectorTrie3(level, size, zero, trieSparsity, e0, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e1)
        }

        //
        // add at index 2
        //
        val sizeOfChild = if(index < ((size / childSize) * childSize)) childSize else (size % childSize)
        val indexInChild = index % childSize
        return SparseVectorTrie3(level, size, zero, trieSparsity, e0, e1, SparseVectors.ofSize(level - 1, sizeOfChild, zero).set(indexInChild, value))
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // short circuit if the value we intend to clear is already zero
        //
        return if(zero == this.get(index)) this else innerClear(index)
    }


    /**
     * internal implementation of clear that
     * presumes the index is in bounds
     * and the value to be cleared is non-zero
     *
     * @param index must be in bounds and reference and non-zero value
     * @return a new vector with the value cleared to zero
     */
    internal fun innerClear(index: Int): Vector<T>
    {
        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        return when (i)
        {
            0 -> SparseVectorTrie1(level, size, zero, sparsity and (1 shl trieIndex).inv(), e1)
            1 -> SparseVectorTrie1(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0)
            else -> this
        }
    }

}

class SparseVectorTrie3<T>(val level: Int, val size: Int, val zero: T, val sparsity: Int, val e0: Vector<T>, val e1: Vector<T>, val e2: Vector<T>) : Vector<T>
{
    val childSize = IntegerMath.power(NODE_SIZE, level)

    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        when (sparseIndex(sparsity, index / childSize))
        {
            0 -> return e0.get(index % childSize)
            1 -> return e1.get(index % childSize)
            2 -> return e2.get(index % childSize)
        }

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        when (sparseIndex(sparsity, trieIndex))
        {
            0 -> return SparseVectorTrie3(level, size, zero, sparsity, e0.set(index % childSize, value), e1, e2)
            1 -> return SparseVectorTrie3(level, size, zero, sparsity, e0, e1.set(index % childSize, value), e2)
            2 -> return SparseVectorTrie3(level, size, zero, sparsity, e0, e1, e2.set(index % childSize, value))
        }

        //
        // index corresponds to a zero element in this trie, so we are adding a new non-zero element (a new child vector)
        // calculate the new elements sparsity bits and index
        // and create a new sparse vector with an additional element
        //
        val trieSparsity = sparsity or (1 shl trieIndex) // sparsity at this trie level
        when (sparseIndex(trieSparsity, trieIndex))
        {
            0 -> return SparseVectorTrie4(level, size, zero, trieSparsity, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e0, e1, e2)
            1 -> return SparseVectorTrie4(level, size, zero, trieSparsity, e0, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e1, e2)
            2 -> return SparseVectorTrie4(level, size, zero, trieSparsity, e0, e1, SparseVectors.ofSize(childSize, zero).set(index % childSize, value), e2)
        }

        //
        // add at index 3
        //
        val sizeOfChild = if(index < ((size / childSize) * childSize)) childSize else (size % childSize)
        val indexInChild = index % childSize
        return SparseVectorTrie4(level, size, zero, trieSparsity, e0, e1, e2, SparseVectors.ofSize(level - 1, sizeOfChild, zero).set(indexInChild, value))
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // short circuit if the value we intend to clear is already zero
        //
        return if(zero == this.get(index)) this else innerClear(index)
    }


    /**
     * internal implementation of clear that
     * presumes the index is in bounds
     * and the value to be cleared is non-zero
     *
     * @param index must be in bounds and reference and non-zero value
     * @return a new vector with the value cleared to zero
     */
    internal fun innerClear(index: Int): Vector<T>
    {
        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        return when (i)
        {
            0 -> SparseVectorTrie2(level, size, zero, sparsity and (1 shl trieIndex).inv(), e1, e2)
            1 -> SparseVectorTrie2(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0, e2)
            2 -> SparseVectorTrie2(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0, e1)
            else -> this
        }
    }
}

//
// this is 'Dense' SparseVectorTrie in that it has NODE_SIZE non-zero elements
//
class SparseVectorTrie4<T>(val level: Int, val size: Int, val zero: T, val sparsity: Int, val e0: Vector<T>, val e1: Vector<T>, val e2: Vector<T>, val e3: Vector<T>) : Vector<T>
{
    val childSize = IntegerMath.power(NODE_SIZE, level)

    override fun isEmpty() = false;

    override fun size(): Int = size

    override fun get(index: Int): T
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        //
        val trieIndex = index / childSize
        when (sparseIndex(sparsity, index / childSize))
        {
            0 -> return e0.get(index % childSize)
            1 -> return e1.get(index % childSize)
            2 -> return e2.get(index % childSize)
            3 -> return e3.get(index % childSize)
        }

        return zero
    }

    override fun set(index: Int, value: T): Vector<T>
    {
        if(zero == value)
        {
            //
            // the value is being set to zero, so clear it
            //
            return clear(index);
        }

        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        when (trieIndex)
        {
            0 -> return SparseVectorTrie4(level, size, zero, sparsity, e0.set(index % childSize, value), e1, e2, e3)
            1 -> return SparseVectorTrie4(level, size, zero, sparsity, e0, e1.set(index % childSize, value), e2, e3)
            2 -> return SparseVectorTrie4(level, size, zero, sparsity, e0, e1, e2.set(index % childSize, value), e3)
            3 -> return SparseVectorTrie4(level, size, zero, sparsity, e0, e1, e2, e3.set(index % childSize, value))
        }

        //
        // this is a full trie, so their are no zero elements
        //
        throw IndexOutOfBoundsException();
    }

    override fun clear(index: Int): Vector<T>
    {
        if ((index < 0) || (index >= size)) throw IndexOutOfBoundsException()

        //
        // short circuit if the value we intend to clear is already zero
        //
        return if(zero == this.get(index)) this else innerClear(index)
    }


    /**
     * internal implementation of clear that
     * presumes the index is in bounds
     * and the value to be cleared is non-zero
     *
     * @param index must be in bounds and reference and non-zero value
     * @return a new vector with the value cleared to zero
     */
    internal fun innerClear(index: Int): Vector<T>
    {
        //
        // calculate number of 1 bits in sparsity, then use this to get offset of non-zero element
        // if it corresponds to a current element, then this is an update
        //
        val trieIndex = index / childSize
        val i = sparseIndex(sparsity, trieIndex)
        return when (i)
        {
            0 -> SparseVectorTrie3(level, size, zero, sparsity and (1 shl trieIndex).inv(), e1, e2, e3)
            1 -> SparseVectorTrie3(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0, e2, e3)
            2 -> SparseVectorTrie3(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0, e1, e3)
            3 -> SparseVectorTrie3(level, size, zero, sparsity and (1 shl trieIndex).inv(), e0, e1, e2)
            else -> this
        }
    }
}

/**
 * Calculate element index in sparse array given it's natural index and the sparsity bits
 *
 * @param sparsity bits indicating which elements are non-zero
 * @param index natural index of the element
 */
private fun sparseIndex(sparsity: Int, index: Int): Int
{
    //
    // if the node is full, then index is already correct because it is not compressed
    //
    if(NODE_BITS == sparsity) return index;

    //
    // the first bit must be a one
    //
    var bit = 1 shl index
    if (0 == bit and sparsity) return -1;

    //
    // to get the index into the non-zero elements
    // count the number of one bits, then subtract one
    //
    bit = bit ushr 1
    var count = 1
    for (i in (index - 1) downTo 0)
    {
        if (0 != (bit and sparsity))
        {
            count += 1
        }
        bit = bit ushr 1
    }
    return count - 1
}

/**
 * Calculate the depth of the tree given the vector size
 *
 * @param size the size of the vector (the non-negative number of elements in the vector)
 * @return the positive depth of a tree of the given size
 */
fun depth(size: Int): Int
{
    tailrec fun innerDepth(size: Int, level: Int, levelSize: Int): Int
    {
        return if(size > levelSize) innerDepth(size, level + 1, levelSize * NODE_SIZE) else level
    }

    return innerDepth(size, 0, NODE_SIZE)
}

