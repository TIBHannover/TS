package uk.ac.ebi.spot.ols.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CollectionUtils {
    public static <T> Set<T> intersection(List<Set<T>> sets) {
        if (sets.isEmpty()) {
            return new HashSet<>();
        }

        return sets.stream()
            .skip(1)
            .collect(() -> new HashSet<>(sets.get(0)), Set::retainAll, Set::retainAll);
    }
}
