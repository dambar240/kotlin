// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: STDLIB_COLLECTIONS
// IGNORE_BACKEND_FIR: JVM_IR
// KJS_WITH_FULL_RUNTIME
// IGNORE_BACKEND: NATIVE

interface A : Set<String>

class B : A, HashSet<String>()

fun box(): String {
    val b = B()
    b.add("OK")
    return b.iterator().next()
}
