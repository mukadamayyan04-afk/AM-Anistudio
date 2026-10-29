package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AIProviderConfigEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RenderJobEntity
import com.example.data.model.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY name ASC")
    fun getCharactersForProject(projectId: String): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE projectId = :projectId")
    suspend fun deleteAllForProject(projectId: String)
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY sceneIndex ASC")
    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE projectId = :projectId AND episodeId = :episodeId ORDER BY sceneIndex ASC")
    fun getScenesForEpisode(projectId: String, episodeId: String): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scenes: List<SceneEntity>)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Delete
    suspend fun deleteScene(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE projectId = :projectId")
    suspend fun deleteAllForProject(projectId: String)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE projectId = :projectId ORDER BY episodeNumber ASC")
    fun getEpisodesForProject(projectId: String): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Update
    suspend fun updateEpisode(episode: EpisodeEntity)

    @Delete
    suspend fun deleteEpisode(episode: EpisodeEntity)
}

@Dao
interface RenderJobDao {
    @Query("SELECT * FROM render_jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<RenderJobEntity>>

    @Query("SELECT * FROM render_jobs WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getJobsForProject(projectId: String): Flow<List<RenderJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: RenderJobEntity)

    @Update
    suspend fun updateJob(job: RenderJobEntity)

    @Delete
    suspend fun deleteJob(job: RenderJobEntity)

    @Query("DELETE FROM render_jobs WHERE id = :id")
    suspend fun deleteJobById(id: String)
}

@Dao
interface AIProviderDao {
    @Query("SELECT * FROM provider_configs")
    fun getAllProviders(): Flow<List<AIProviderConfigEntity>>

    @Query("SELECT * FROM provider_configs WHERE providerId = :id")
    suspend fun getProviderById(id: String): AIProviderConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: AIProviderConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<AIProviderConfigEntity>)

    @Query("UPDATE provider_configs SET apiKey = '', maskedKey = '', lastStatus = 'Not Configured' WHERE providerId = :id")
    suspend fun removeCredential(id: String)
}

@Dao
interface CapabilityRoutingDao {
    @Query("SELECT * FROM capability_routing")
    fun getAllRoutings(): Flow<List<com.example.data.model.CapabilityRoutingEntity>>

    @Query("SELECT providerId FROM capability_routing WHERE capability = :capability")
    suspend fun getProviderForCapability(capability: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRouting(routing: com.example.data.model.CapabilityRoutingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAll(routings: List<com.example.data.model.CapabilityRoutingEntity>)
}

