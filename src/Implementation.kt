import java.math.BigInteger

class Rational(val num: BigInteger, val denom: BigInteger) {
    companion object {
        private fun minCommonDivisor(denominatorA: BigInteger, denominatorB: BigInteger): BigInteger {
            val maxCommonDivisor = denominatorA.gcd(denominatorB)
            return denominatorA * (denominatorB / maxCommonDivisor)
        }
        fun toSameBase(first: Rational, second: Rational): Pair<Rational, Rational> {
            val mmc = minCommonDivisor(first.denom, second.denom)
            val firstEquivalentRational = Rational(first.toEquivalentNumerator(mmc), mmc)
            val secondEquivalentRational = Rational(second.toEquivalentNumerator(mmc), mmc)
            return firstEquivalentRational to secondEquivalentRational
        }
    }
    fun toEquivalentNumerator(mmc: BigInteger) = this.num * (mmc / this.denom)

    /*
    * The denominator must be always positive in the normalized form and
    * if the negative rational is normalized, then only the numerator can be negative
    */
    fun normalize(): Rational {
        if (num == BigInteger.ZERO) return this
        val maxDivider = num.gcd(denom)
        val normalized = Rational(
            num = num / maxDivider,
            denom = (denom / maxDivider).abs()
        )
        return when {
            denom < BigInteger.ZERO -> -normalized
            else -> normalized
        }
    }
    override fun toString(): String {
        return when (denom) {
            BigInteger.ONE -> num.toString()
            else -> "$num/$denom"
        }
    }
    override fun equals(other: Any?) = when (other) {
        is Rational -> this.num == other.num && this.denom == other.denom
        else -> super.equals(other)
    }
    override fun hashCode(): Int {
        var result = num.hashCode()
        result = 31 * result + denom.hashCode()
        return result
    }
    operator fun compareTo(rational: Rational): Int {
        val (sameBaseThis, sameBaseTarget) = toSameBase(this, rational)
        return when {
            sameBaseThis.num > sameBaseTarget.num -> 1
            sameBaseThis.num < sameBaseTarget.num -> -1
            else -> 0
        }
    }
    operator fun unaryMinus(): Rational {
        return Rational(-num, denom)
    }
    operator fun rangeTo(rational: Rational): Pair<Rational, Rational> {
        return Pair(this, rational)
    }
    operator fun plus(target: Rational): Rational {
        val (sameBaseThis, sameBaseTarget) = toSameBase(this, target)
        return Rational(
            num = sameBaseThis.num + sameBaseTarget.num,
            denom = sameBaseThis.denom
        ).normalize()
    }
    operator fun minus(target: Rational): Rational {
        val (sameBaseThis, sameBaseTarget) = toSameBase(this, target)
        return Rational(
            num = sameBaseThis.num - sameBaseTarget.num,
            denom = sameBaseThis.denom
        ).normalize()
    }
    operator fun times(target: Rational) = Rational(
        num = this.num * target.num,
        denom = this.denom * target.denom
    ).normalize()
    operator fun div(target: Rational) = Rational(
        num = this.num * target.denom,
        denom = this.denom * target.num).normalize()
}
operator fun Pair<Rational, Rational>.contains(rational: Rational): Boolean {
    return rational >= this.first && rational < this.second
}
fun String.toRational(): Rational {
    return if (this.contains("/")) {
        val (num, den) = this.split("/")
        Rational(num.toBigInteger(), den.toBigInteger()).normalize()
    } else {
        Rational(this.toBigInteger(), BigInteger.ONE)
    }
}
infix fun Number.divBy(first: Number) = Rational(this.toString().toBigInteger(), first.toString().toBigInteger()).normalize()