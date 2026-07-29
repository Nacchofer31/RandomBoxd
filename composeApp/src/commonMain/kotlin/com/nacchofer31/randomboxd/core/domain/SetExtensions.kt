package com.nacchofer31.randomboxd.core.domain

/**
 * Returns a random element from the set, excluding the specified element if possible.
 * Comparison is done by the provided key function instead of object equality.
 * If [exclude] is null, returns a random element without filtering.
 * If the set contains only the excluded element, returns a random element.
 *
 * @param exclude The element to exclude from the random selection, or null to not exclude anything
 * @param keySelector Function to extract the comparison key from each element
 * @return A random element from the set, preferably different from [exclude]
 * @throws NoSuchElementException if the set is empty
 */
fun <T, K> Set<T>.randomExcluding(
    exclude: T? = null,
    keySelector: (T) -> K,
): T {
    if (isEmpty()) throw NoSuchElementException("Set is empty.")
    if (exclude == null) return random()

    val excludeKey = keySelector(exclude)
    val candidates = filter { keySelector(it) != excludeKey }
    return if (candidates.isNotEmpty()) {
        candidates.random()
    } else {
        // Only the excluded element exists, return it
        random()
    }
}
