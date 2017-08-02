package com.lumpofcode.math

import org.junit.Test
import org.junit.Assert.*

/**
 * Created by emurphy on 7/19/17.
 */
class IntegerMathTest
{
    @Test
    fun powerTest()
    {
        assertTrue(1 == IntegerMath.power(1, 2))
        assertTrue(4 == IntegerMath.power(2, 2))
        assertTrue(9 == IntegerMath.power(3, 2))
        assertTrue(27 == IntegerMath.power(3, 3))
        assertTrue(100 == IntegerMath.power(10, 2))
        assertTrue(1000 == IntegerMath.power(10, 3))
        assertTrue(10000 == IntegerMath.power(10, 4))
        assertTrue(100000 == IntegerMath.power(10, 5))
        assertTrue(1000000 == IntegerMath.power(10, 6))
        assertTrue(10000000 == IntegerMath.power(10, 7))
        assertTrue(100000000 == IntegerMath.power(10, 8))
        assertTrue(1000000000 == IntegerMath.power(10, 9))
    }

}
