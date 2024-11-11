package com.example.gitloader.di

import android.content.Context
import androidx.room.Room
import com.example.gitloader.data.DatabaseRepositoryImpl
import com.example.gitloader.db.AppDatabase
import com.example.gitloader.db.RepositoryDao
import com.example.gitloader.domain.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "repository_database"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideRepositoryDao(appDatabase: AppDatabase): RepositoryDao {
        return appDatabase.repositoryDao()
    }

    @Provides
    fun provideDatabaseRepository(repositoryDao: RepositoryDao): DatabaseRepository {
        return DatabaseRepositoryImpl(repositoryDao)
    }
}