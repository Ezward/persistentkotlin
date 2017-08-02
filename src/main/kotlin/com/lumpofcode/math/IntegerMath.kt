package com.lumpofcode.math

/**
 * Created by emurphy on 7/12/17.
 */
object IntegerMath
{
    /**
     * Calculate power of an non-negative integer.
     *
     * NOTE: no overflow checking is done.
     *
     * @param i the number to raise to the power
     * @param exponent
     * @return the i ^ exponent or 0 if exponent is negative
     * @throws IllegalArgumentException if exponent < 0
     */
    public fun power(i: Int, exponent: Int): Int
    {
        if(exponent < 0) throw IllegalArgumentException();

        //
        // Uses recursive power by squares algorithm
        //
        tailrec fun innerPower(accumulator: Int, number: Int, power: Int): Int
        {
            return when(power)
            {
                0 -> accumulator
                1 -> number * accumulator
                else -> when(power.mod(2))
                {
                    0 -> innerPower(accumulator, number * number, power / 2)
                    else -> innerPower(number * accumulator, number * number, (power - 1) / 2)
                }
            }
        }

        // hack - return zero if exponent is negative
        return if(exponent >= 0) innerPower(1, i, exponent) else 0;
    }

}