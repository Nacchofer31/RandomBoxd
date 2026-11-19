package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

/**
 * Interpretation of the username textfield while the list picker is open.
 *
 * Selecting a Letterboxd list types `username/slug`, so a `/` opens the list picker for `username` and the
 * text typed after it filters the scrapped lists.
 *
 * A second `/` means the user is typing a whole new username without clearing the field first, e.g.
 * `alice/bob/que` means "scrap bob's lists and filter them by `que`". This matters because the textfield is
 * not cleared after an empty search.
 *
 * Edge case: after a list is selected the field holds `alice/slug`, so typing a trailing `/` (`alice/slug/`)
 * re-parses as list user `slug`. That is accepted on purpose: a list filter never legitimately contains a
 * `/`, and the film repository only understands exactly two segments (`user/list`) anyway.
 */
data class ParsedUserNameInput(
    /** Username whose lists must be scrapped, or `null` when the picker has to stay hidden. */
    val listUser: String?,
    /** Text typed after [listUser], used to filter the scrapped lists. */
    val listQuery: String,
) {
    companion object {
        private const val SEPARATOR = '/'

        fun from(userName: String): ParsedUserNameInput {
            val firstSeparator = userName.indexOf(SEPARATOR)
            if (firstSeparator < 0) return ParsedUserNameInput(listUser = null, listQuery = "")

            val secondSeparator = userName.indexOf(SEPARATOR, startIndex = firstSeparator + 1)
            if (secondSeparator < 0) {
                return ParsedUserNameInput(
                    listUser = userName.take(firstSeparator).toListUser(),
                    listQuery = userName.substring(firstSeparator + 1),
                )
            }

            return ParsedUserNameInput(
                listUser = userName.substring(firstSeparator + 1, secondSeparator).toListUser(),
                listQuery = userName.substring(secondSeparator + 1),
            )
        }

        private fun String.toListUser(): String? = trim().ifBlank { null }
    }
}
