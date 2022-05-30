package rationals

import java.math.BigInteger
import java.math.BigInteger.*

infix fun BigInteger.divBy(denom: BigInteger) =
    Rational.create(this, denom)

infix fun Int.divBy(denom: Int) =
    Rational.create(this.toBigInteger(), denom.toBigInteger())

infix fun Long.divBy(denom: Long) =
    Rational.create(this.toBigInteger(), denom.toBigInteger())


fun String.toRational(): Rational {
    fun String.toBigIntegerOrFail() =
        toBigIntegerOrNull() ?: throw IllegalArgumentException(
            "Excepting rational in the form of 'n/d' or 'n', " +
                    "was: '${this@toRational}'")

    if (!contains('/')) {
        return Rational.create(toBigIntegerOrFail(), ONE)
    }
    val (numer, denom) = split('/')
    return Rational.create(
        numer.toBigIntegerOrFail(), denom.toBigIntegerOrFail())
}


@Suppress("DataClassPrivateConstructor")
data class Rational
private constructor(val numer: BigInteger, val denom: BigInteger) : Comparable<Rational> {


    companion object {

        fun normalize(n: BigInteger, d: BigInteger): Rational {
            require(d != ZERO) { "Denominator must be non-zero" }
            val g = n.gcd(d)
            val sign = d.signum().toBigInteger()
            return Rational(n / g * sign, d / g * sign)
        }

        fun create(n: BigInteger, d: BigInteger): Rational =
            normalize(n, d)
    }

    operator fun plus(other: Rational): Rational = create(
        numer * other.denom + other.numer * denom,
        denom * other.denom
    )

    operator fun minus(other: Rational): Rational = create(
        numer * other.denom - other.numer * denom,
        denom * other.denom
    )

    operator fun times(other: Rational): Rational = create(
        numer * other.numer,
        denom * other.denom
    )

    operator fun div(other: Rational): Rational = create(
        numer * other.denom,
        denom * other.numer
    )

    operator fun unaryMinus(): Rational = create(-numer, denom)

    override fun compareTo(other: Rational): Int {
        return (numer * other.denom - other.numer * denom).signum()
    }

    override fun toString(): String {
        return if (denom == ONE)
            "$numer"
        else
            "$numer/$denom"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numer != other.numer) return false
        if (denom != other.denom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numer.hashCode()
        result = 31 * result + denom.hashCode()
        return result
    }
}

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println(
        "912016490186296920119201192141970416029".toBigInteger() divBy
                "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2
    )
}

