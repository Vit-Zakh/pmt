package com.cevt.pmh.manager

import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

const val DEFAULT_RATE = 0F

const val DEVICE_PROJECT_CODE_PROPERTY_ID = 554725667
const val VIN_NUM = 557936774
const val IGNITION_PROPERTY_ID = 557871385

class CarPropertyManagerHelper @Inject constructor(
    val carPropertyManager: CarPropertyManager,
) {

    fun <T, R> generatePropertyFlowById(propertyId: Int, mapper: (T?) -> R): Flow<R> =
        callbackFlow {
            val callback = object : SimpleEventPropertyListener() {
                override fun onChangeEvent(value: CarPropertyValue<*>?) {
                    trySend(mapper.invoke(value?.value as? T))
                }
            }

            carPropertyManager.registerCallback(callback, propertyId, DEFAULT_RATE)
            awaitClose { carPropertyManager.unregisterCallback(callback) }
        }

    fun <T> generatePropertyFlowById(propertyId: Int): Flow<T> =
        callbackFlow {
            val callback = object : SimpleEventPropertyListener() {
                override fun onChangeEvent(value: CarPropertyValue<*>?) {
                    (value?.value as? T)?.let { trySend(it) }
                }
            }
            carPropertyManager.registerCallback(callback, propertyId, DEFAULT_RATE)
            awaitClose {
                carPropertyManager.unregisterCallback(callback)
            }
        }

    suspend fun <T, R> getPropertyFlowById(propertyId: Int, mapper: (T?) -> R): R {
        return suspendCoroutine {
            val callback = object : SimpleEventPropertyListener() {
                override fun onChangeEvent(value: CarPropertyValue<*>?) {
                    it.resumeWith(Result.success(mapper.invoke(value?.value as? T)))
                    carPropertyManager.unregisterCallback(this)
                }
            }
            carPropertyManager.registerCallback(callback, propertyId, DEFAULT_RATE)
        }
    }

    suspend fun <T> getPropertyFlowById(propertyId: Int): T {
        return suspendCoroutine { continuation ->
            val callback = object : SimpleEventPropertyListener() {
                override fun onChangeEvent(value: CarPropertyValue<*>?) {
                    Log.d("PROPERTY_TAG", "property with id $propertyId was changed ")
                    (value?.value as? T)?.let {
                        Log.d("PROPERTY_TAG", "new value of $propertyId is ${it.toString()} ")
                        continuation.resumeWith(Result.success(it))
                        carPropertyManager.unregisterCallback(this)
                    }
                }
            }
            carPropertyManager.registerCallback(callback, propertyId, DEFAULT_RATE)
        }
    }

    suspend fun getDeviceProjectCode() =
        getPropertyFlowById<String>(DEVICE_PROJECT_CODE_PROPERTY_ID)


    fun subscribeToVin() =
        generatePropertyFlowById<Int>(VIN_NUM)

    fun getPropertyList() = carPropertyManager.propertyList


    enum class IgnitionStatus {
        Unknown,
        On,
        Off;
    }

    val ignitionStatusFlow: Flow<IgnitionStatus> = callbackFlow {
        val callback = object : SimpleEventPropertyListener() {
            override fun onChangeEvent(value: CarPropertyValue<*>?) {
                trySend((value?.value as? Int?).mapToIgnition())
            }
        }

        carPropertyManager.registerCallback(callback, IGNITION_PROPERTY_ID, 0F)
        awaitClose { carPropertyManager.unregisterCallback(callback) }
    }

    // todo: this needs to be rechecked
    private fun Int?.mapToIgnition(): IgnitionStatus = when (this) {
        null, 0, 1 -> IgnitionStatus.Off
        else -> IgnitionStatus.On
    }

    private fun log(message: String) {
        Log.d(CarPropertyManagerHelper::class.simpleName, message)
    }
}


abstract class SimpleEventPropertyListener : CarPropertyManager.CarPropertyEventCallback {
    override fun onErrorEvent(p0: Int, p1: Int) {
        Log.e(this::class.java.simpleName, "could not get event info $p0, $p1")
    }
}

