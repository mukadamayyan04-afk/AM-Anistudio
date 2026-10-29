package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AIProviderConfigEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RenderJobEntity
import com.example.data.model.SceneEntity

@Database(
    entities = [
        ProjectEntity::class,
        CharacterEntity::class,
        SceneEntity::class,
        EpisodeEntity::class,
        RenderJobEntity::class,
        AIProviderConfigEntity::class,
        com.example.data.model.CapabilityRoutingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun characterDao(): CharacterDao
    abstract fun sceneDao(): SceneDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun renderJobDao(): RenderJobDao
    abstract fun aiProviderDao(): AIProviderDao
    abstract fun capabilityRoutingDao(): CapabilityRoutingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amanistudio_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
