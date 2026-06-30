package it.archesurvey.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.archesurvey.app.data.local.dao.ProjectDao
import it.archesurvey.app.data.local.dao.SurveyDao
import it.archesurvey.app.data.local.entity.ProjectEntity
import it.archesurvey.app.data.local.entity.SurveyEntity

@Database(
    entities = [
        ProjectEntity::class,
        SurveyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ArcheSurveyDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun surveyDao(): SurveyDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE projects ADD COLUMN createdAtMillis INTEGER")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS surveys (
                        id TEXT NOT NULL,
                        projectId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        createdAtMillis INTEGER,
                        PRIMARY KEY(id),
                        FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_surveys_projectId ON surveys(projectId)")
            }
        }
    }
}
