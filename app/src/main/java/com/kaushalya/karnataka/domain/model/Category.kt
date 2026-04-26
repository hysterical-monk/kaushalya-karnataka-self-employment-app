package com.kaushalya.karnataka.domain.model

enum class Category(val id: String, val displayName: String) {
    ELECTRICIAN("electrician", "Electrician"),
    PLUMBER("plumber", "Plumber"),
    CARPENTER("carpenter", "Carpenter"),
    PAINTER("painter", "Painter"),
    MASON("mason", "Mason"),
    MECHANIC("mechanic", "Mechanic"),
    APPLIANCE_REPAIR("appliance_repair", "Appliance Repair"),
    TAILOR("tailor", "Tailor"),
    GARDENER("gardener", "Gardener"),
    DRIVER("driver", "Driver"),
    OTHER("other", "Other");

    companion object {
        fun fromId(id: String): Category = entries.firstOrNull { it.id == id } ?: OTHER
    }
}
