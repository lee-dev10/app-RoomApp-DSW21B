package com.example.roomapp.data.model

enum class TaskPriority(val label: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

enum class TaskStatus(val label: String) {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Progreso"),
    COMPLETADA("Completada")
}

enum class TaskCategory(val label: String) {
    TODAS("Todas"),
    TRABAJO("Trabajo"),
    PERSONAL("Personal"),
    ESTUDIOS("Estudios"),
    HOGAR("Hogar"),
    OTRO("Otro")
}
