package it.archesurvey.app.data.local.mapper

import it.archesurvey.app.data.local.entity.SurveyEntity
import it.archesurvey.app.domain.model.Survey

fun SurveyEntity.toDomain(): Survey {
    return Survey(
        id = id,
        projectId = projectId,
        title = title,
        notes = notes,
        createdAtMillis = createdAtMillis
    )
}

fun Survey.toEntity(): SurveyEntity {
    return SurveyEntity(
        id = id,
        projectId = projectId,
        title = title,
        notes = notes,
        createdAtMillis = createdAtMillis
    )
}
