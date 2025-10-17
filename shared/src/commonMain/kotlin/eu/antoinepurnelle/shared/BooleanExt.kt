package eu.antoinepurnelle.shared

/** Returns this Boolean if it's not `null` and *true* otherwise. */
fun Boolean?.orTrue() = this ?: true

/** Returns this Boolean if it's not `null` and *false* otherwise. */
fun Boolean?.orFalse() = this ?: false
