package it.archesurvey.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import it.archesurvey.app.data.local.dao.ProjectDao
import it.archesurvey.app.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ArcheSurveyDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
