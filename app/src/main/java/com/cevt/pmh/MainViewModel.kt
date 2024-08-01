package com.cevt.pmh

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cevt.pmh.manager.CarPropertyManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val carPropertyManagerHelper: CarPropertyManagerHelper,
) : ViewModel() {

    var deviceProjectCode by mutableStateOf<String>("")
        private set

    var stringProperty by mutableStateOf<String>("")
        private set

    var integerProperty by mutableStateOf<String>("")
        private set

    fun getDeviceProjectCode() = viewModelScope.launch {
        deviceProjectCode = carPropertyManagerHelper.getDeviceProjectCode()
    }

    val subscribedProperty = carPropertyManagerHelper.subscribeToVin()

    val propertyList = carPropertyManagerHelper.getPropertyList().forEach {
        Log.d("PROPERTY_TAG", "available property: ${it.toString()}")
    }

    val ignition = carPropertyManagerHelper.ignitionStatusFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CarPropertyManagerHelper.IgnitionStatus.Off
    )

    fun getStringProperty(propertyId: Int?) = viewModelScope.launch {
        if (propertyId == null) return@launch
        Log.d("PROPERTY_TAG", "getStringProperty:$propertyId ")
        stringProperty = carPropertyManagerHelper.getPropertyFlowById<String>(propertyId)
    }

    fun getIntegerProperty(propertyId: Int?) = viewModelScope.launch {
        if (propertyId == null) return@launch
        Log.d("PROPERTY_TAG", "getIntegerProperty:$propertyId ")
        integerProperty = carPropertyManagerHelper.getPropertyFlowById<Int>(propertyId).toString()
    }
}