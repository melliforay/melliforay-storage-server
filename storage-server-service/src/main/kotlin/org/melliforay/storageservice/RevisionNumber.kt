package org.melliforay.storageservice

import java.math.BigInteger

/**
 * Revision numbers are used to track the verion numbers for [Session]s, [Node]s, and the repository.
 * They are represented as hex strings.
 */
class RevisionNumber(private val number: BigInteger): Comparable<RevisionNumber> {

    constructor(hexString: String): this(BigInteger(hexString, 16))

    constructor(i: Int): this(BigInteger(i.toString(), 10))

    operator fun plus(i: Int): RevisionNumber = RevisionNumber(number.add(BigInteger(i.toString())))

    operator fun minus(i: Int): RevisionNumber = RevisionNumber(number.minus(BigInteger(i.toString())))

    override fun toString(): String = number.toString(16)

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            is RevisionNumber -> number == other.number
            is Int -> number.intValueExact() == other
            is Long -> number.longValueExact() == other
            is BigInteger -> number == other
            else -> false
        }
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

    override fun compareTo(other: RevisionNumber): Int = number.compareTo(other.number)

}