// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: STDLIB_COLLECTIONS
// KJS_WITH_FULL_RUNTIME
// Auto-generated by org.jetbrains.kotlin.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
// WITH_RUNTIME



fun box(): String {
    val list1 = ArrayList<Int>()
    val range1 = (3..5).reversed()
    for (i in range1) {
        list1.add(i)
        if (list1.size > 23) break
    }
    if (list1 != listOf<Int>(5, 4, 3)) {
        return "Wrong elements for (3..5).reversed(): $list1"
    }

    val list2 = ArrayList<Int>()
    val range2 = (3.toShort()..5.toShort()).reversed()
    for (i in range2) {
        list2.add(i)
        if (list2.size > 23) break
    }
    if (list2 != listOf<Int>(5, 4, 3)) {
        return "Wrong elements for (3.toShort()..5.toShort()).reversed(): $list2"
    }

    val list3 = ArrayList<Long>()
    val range3 = (3L..5L).reversed()
    for (i in range3) {
        list3.add(i)
        if (list3.size > 23) break
    }
    if (list3 != listOf<Long>(5, 4, 3)) {
        return "Wrong elements for (3L..5L).reversed(): $list3"
    }

    val list4 = ArrayList<Char>()
    val range4 = ('a'..'c').reversed()
    for (i in range4) {
        list4.add(i)
        if (list4.size > 23) break
    }
    if (list4 != listOf<Char>('c', 'b', 'a')) {
        return "Wrong elements for ('a'..'c').reversed(): $list4"
    }

    return "OK"
}
