package com.cevt.pmh.di

import android.car.Car
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun providesCarPropertyManagerr(
        @ApplicationContext context: Context,
    ): CarPropertyManager = Car.createCar(context).getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
}