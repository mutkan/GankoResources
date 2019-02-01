package com.ceotic.ganko.data

import com.ceotic.ganko.data.models.Servicio

data class EmptyDaysValidations(
        var services: List<Servicio>,
        var activeServices:List<Servicio>,
        var confirmedFinishedServices:List<Servicio>,
        var emptyDays:Long
)