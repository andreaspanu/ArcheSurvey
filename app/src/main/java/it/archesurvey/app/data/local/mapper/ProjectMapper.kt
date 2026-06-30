package it.archesurvey.app.data.local.mapper

import it.archesurvey.app.data.local.entity.ProjectEntity
import it.archesurvey.app.domain.model.Project

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        name = name,
        client = client,
        location = location,
        notes = notes,
        createdAtMillis = createdAtMillis
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        client = client,
        location = location,
        notes = notes,
        createdAtMillis = createdAtMillis
    )
}
