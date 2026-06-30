package it.archesurvey.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.archesurvey.app.data.local.entity.SurveyEntity

@Dao
interface SurveyDao {
    @Query("SELECT * FROM surveys WHERE projectId = :projectId ORDER BY createdAtMillis DESC")
    suspend fun getSurveys(projectId: String): List<SurveyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvey(survey: SurveyEntity)

    @Query("DELETE FROM surveys WHERE id = :surveyId")
    suspend fun deleteSurvey(surveyId: String)
}
