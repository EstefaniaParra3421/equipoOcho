package com.univalle.inventorywidget.di

import com.google.firebase.auth.FirebaseAuth
import com.univalle.inventorywidget.data.datasource.FirebaseAuthDataSource
import com.univalle.inventorywidget.data.repository.AuthRepository
import com.univalle.inventorywidget.domain.repository.AuthRepositoryImpl
import com.univalle.inventorywidget.domain.usecase.LoginUseCase
import com.univalle.inventorywidget.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDataSource(auth: FirebaseAuth): FirebaseAuthDataSource =
        FirebaseAuthDataSource(auth)

    @Provides
    @Singleton
    fun provideRepository(dataSource: FirebaseAuthDataSource): AuthRepository =
        AuthRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repo: AuthRepository) = RegisterUseCase(repo)
}
