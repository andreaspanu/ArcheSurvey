package it.archesurvey.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.archesurvey.app.data.local.dao.ProjectDao
import it.archesurvey.app.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ArcheSurveyDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE projects ADD COLUMN createdAtMillis INTEGER")
            }
        }
    }
}
