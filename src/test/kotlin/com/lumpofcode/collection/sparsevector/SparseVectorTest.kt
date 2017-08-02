package com.lumpofcode.collection.sparsevector

import org.junit.Test
import org.junit.Assert.*

/**
 * Created by emurphy on 7/19/17.
 */
class SparseVectorTest
{
    @Test
    fun testEmptyVector()
    {
        val emptyVector: Vector<Int> = SparseVectors.ofSize(0, 0)

        assertTrue("Should be empty", emptyVector.isEmpty())
        assertEquals("size should be zero", 0, emptyVector.size())

        //
        // get and set should throw because every index is out of bounds
        //
        try
        {
            emptyVector.get(0);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            emptyVector.get(-1);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            emptyVector.set(0, 1);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }
        try
        {
            emptyVector.set(-1, 1);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

    }

    @Test
    fun testEmptyVectorOf1()
    {
        val n = 1
        val vector: Vector<Int> = SparseVectors.ofSize(1, 0)

        assertFalse("Should NOT be empty", vector.isEmpty())
        assertEquals("size should be $n", 1, vector.size())

        for(i in 0..n-1)
        {
            vector.set(i, i)
        }
        for(i in 0..n-1)
        {
            assertEquals(i, vector.get(i))
        }

        //
        // get and set should throw because every index is out of bounds
        //
        try
        {
            vector.get(-1);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.get(n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.set(-1, n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.set(n, n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }
    }

    @Test
    fun testVectors()
    {
        for(n in 1..100)
        {
            testVectorOfN(n)
        }
    }


    //
    // set non-zero values from beginning to end of vector
    //
    fun testVectorOfN(n: Int)
    {
        var vector: Vector<Int> = SparseVectors.ofSize(n, 0)

        assertFalse("Should NOT be empty", vector.isEmpty())
        assertEquals("size should be $n", n, vector.size())

        //
        // at this point, all values are zero
        //
        for(i in 0..n-1)
        {
            assertEquals("Zero value of index $i in vector of $n", 0, vector.get(i))
        }


        for(i in 0..n-1)
        {
            vector = vector.set(i, i)
        }
        for(i in 0..n-1)
        {
            assertEquals("Index $i in vector of $n", i, vector.get(i))
        }

        //
        // get and set should throw because every index is out of bounds
        //
        try
        {
            vector.get(-1);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.get(n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.set(-1, n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

        try
        {
            vector.set(n, n);
            fail("Out of bounds should throw")
        }
        catch(e: IndexOutOfBoundsException)
        {
            assertTrue(true)
        }
        catch(e: Exception)
        {
            fail("Unexpected exception")
        }

    }

    @Test
    fun testReverseVectors()
    {
        for(n in 1..100)
        {
            testReverseVectorOfN(n)
        }
    }


    //
    // set non zero values starting at the end of the vector
    // and move to the start of the vector
    //
    fun testReverseVectorOfN(n: Int)
    {
        var vector: Vector<Int> = SparseVectors.ofSize(n, 0)

        for (i in (n - 1) downTo 0)
        {
            vector = vector.set(i, i)
        }
        for (i in 0..n - 1)
        {
            assertEquals("Index $i in vector of $n", i, vector.get(i))
        }
    }

    @Test
    fun testInsideVectors()
    {
        for(n in 1..100)
        {
            testInsideVectorOfN(n)
        }
    }


    //
    // set values from middle of vector to either end of the vector
    //
    fun testInsideVectorOfN(n: Int)
    {
        var vector: Vector<Int> = SparseVectors.ofSize(n, 0)

        val m = n / 2;
        if((n - m * 2) > 0)// odd number
        {
            //
            // odd value for n
            //
            vector = vector.set(m, m)
            for (i in 1..m)
            {
                vector = vector.set(m - i, m - i)
                vector = vector.set(m + i, m + i)
            }
        }
        else
        {
            //
            // even value for n
            //
            for (i in 0..(m - 1))
            {
                vector = vector.set(m - 1 - i, m - 1 - i)
                vector = vector.set(m + i, m + i)
            }
        }
        for (i in 0..n - 1)
        {
            assertEquals("Index $i in vector of $n", i, vector.get(i))
        }
    }

    @Test
    fun testClearVectors()
    {
        for(n in 1..100)
        {
            testClearInsideVectorOfN(n)
        }
    }


    //
    // set values from middle of vector to either end of the vector
    //
    fun testClearInsideVectorOfN(n: Int)
    {
        println(n)

        var vector: Vector<Int> = SparseVectors.ofSize(n, 0)

        //
        // fill the vector
        //
        for(i in 0..n-1)
        {
            vector = vector.set(i, i)
        }


        //
        // clear values from the middle, outwards
        //
        val m = n / 2;
        if((n - m * 2) > 0)// odd number
        {
            //
            // odd value for n
            //
            vector = vector.set(m, 0)
            for (i in 1..m)
            {
                vector = vector.set(m - i, 0)
                vector = vector.set(m + i, 0)
            }
        }
        else
        {
            //
            // even value for n
            //
            for (i in 0..(m - 1))
            {
                vector = vector.set(m - 1 - i, 0)
                vector = vector.set(m + i, 0)
            }
        }
        for (i in 0..n - 1)
        {
            assertEquals("Index $i in vector of $n", 0, vector.get(i))
        }
    }




}