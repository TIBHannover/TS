package uk.ac.ebi.spot.ols.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class MathUtils {

    public static double divide(double dividend, double divider) {
        return divider == 0 ? 0 : dividend / divider;
    }

    public static double percent(double share, double total) {
        return total == 0 ? 0 : 100 * share / total;
    }
}
