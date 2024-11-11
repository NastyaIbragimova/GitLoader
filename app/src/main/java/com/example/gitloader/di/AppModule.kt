package com.example.gitloader.di

import android.content.Context
import com.example.gitloader.data.utils.FileDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFileDownloader(@ApplicationContext context: Context): FileDownloader {
        return FileDownloader(context)
    }
}