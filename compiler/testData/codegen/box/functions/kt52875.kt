// LAMBDAS: INDY

fun appendTo(s: String) =
   fun Any?.() = s + this

fun box() = appendTo("O").invoke("K")
