package com.example.reinforcement.data.repository

import com.example.reinforcement.data.model.CigaretteData
import java.time.LocalDate

class CigarettesRepository {
    
    // Mapa para almacenar los datos de cigarros por fecha
    private val cigaretteDataMap = mutableMapOf<LocalDate, CigaretteData>()
    
    // Obtener datos de cigarros para una fecha específica
    fun getCigaretteData(date: LocalDate): CigaretteData {
        if (!cigaretteDataMap.containsKey(date)) {
            cigaretteDataMap[date] = CigaretteData(date)
        }
        return cigaretteDataMap[date]!!
    }
    
    // Incrementar el contador de cigarros para una fecha
    fun incrementCigaretteCount(date: LocalDate): Int {
        val data = getCigaretteData(date)
        data.count++
        cigaretteDataMap[date] = data
        return data.count
    }
    
    // Obtener el historial de cigarros para un rango de fechas
    fun getCigaretteHistory(startDate: LocalDate, endDate: LocalDate): List<CigaretteData> {
        val result = mutableListOf<CigaretteData>()
        var currentDate = startDate
        
        while (!currentDate.isAfter(endDate)) {
            result.add(getCigaretteData(currentDate))
            currentDate = currentDate.plusDays(1)
        }
        
        return result
    }
    
    // Restablecer el contador de cigarros para una fecha
    fun resetCigaretteCount(date: LocalDate) {
        cigaretteDataMap[date] = CigaretteData(date)
    }
}