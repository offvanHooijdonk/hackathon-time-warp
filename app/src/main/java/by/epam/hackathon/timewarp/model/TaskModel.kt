package by.epam.hackathon.timewarp.model

import java.util.*

data class TaskModel(var id: Long, var extId: String, var title: String, var source: Source) {

    override fun equals(other: Any?): Boolean {
        return other is TaskModel && id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    enum class Source(code: Int) {
        JIRA(0), TIME(1), TASK(2)
    }
}