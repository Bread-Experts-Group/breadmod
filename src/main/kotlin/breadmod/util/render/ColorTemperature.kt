package breadmod.util.render

import org.joml.Math.clamp
import java.awt.Color
import kotlin.math.max

private val redPoly = doubleArrayOf(
    4.93596077e0, -1.29917429e0,
    1.64810386e-01, -1.16449912e-02,
    4.86540872e-04, -1.19453511e-05,
    1.59255189e-07, -8.89357601e-10
)

private val greenPoly = doubleArrayOf(
    -4.95931720e-01, 1.08442658e0,
    -9.17444217e-01, 4.94501179e-01,
    -1.48487675e-01, 2.49910386e-02,
    -2.21528530e-03, 8.06118266e-05
) to doubleArrayOf(
    3.06119745e0, -6.76337896e-01,
    8.28276286e-02, -5.72828699e-03,
    2.35931130e-04, -5.73391101e-06,
    7.58711054e-08, -4.21266737e-10
)

private val bluePoly = doubleArrayOf(
    4.93997706e-01, -8.59349314e-01,
    5.45514949e-01, -1.81694167e-01,
    4.16704799e-02, -6.01602324e-03,
    4.80731598e-04, -1.61366693e-05
)

// Adapted from https://gist.github.com/stasikos/06b02d18f570fc1eaa9f
fun getRGBFromK(temperature: Float): Color {
    // Used this: https://gist.github.com/paulkaplan/5184275 at the beginning
    // based on http://stackoverflow.com/questions/7229895/display-temperature-as-a-color-with-c
    // this answer: http://stackoverflow.com/a/24856307
    // (so, interpretation of pseudocode in Java)

    val x = max(temperature / 1000.0, 40.0)
    return Color(
        clamp(
            0.0, 1.0,
            if (temperature < 6527) 1.0
            else poly(redPoly, x)
        ).toFloat(),

        clamp(
            0.0, 1.0,
            if (temperature < 850) 0.0
            else if (temperature <= 6600) poly(greenPoly.first, x)
            else poly(greenPoly.second, x)
        ).toFloat(),

        clamp(
            0.0, 1.0,
            if (temperature < 1900) 0.0
            else if (temperature < 6600) poly(bluePoly, x)
            else 1.0
        ).toFloat(),

        1.0F - (temperature / 6600)
    )
}

// Is there a JOML function for this, I wonder?
fun poly(coefficients: DoubleArray, x: Double): Double {
    var result = coefficients[0]
    var xn = x
    for (i in 1 until coefficients.size) {
        result += xn * coefficients[i]
        xn *= x
    }
    return result
}