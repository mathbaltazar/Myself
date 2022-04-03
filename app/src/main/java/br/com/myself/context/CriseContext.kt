package br.com.myself.context

import android.content.Context
import br.com.myself.model.dao.CriseDAO
import br.com.myself.model.entity.Crise
import br.com.myself.ui.crises.CrisesActivity
import br.com.myself.util.Utils
import java.util.*

class CriseContext {
    companion object {
    
    
        private var mDAO: CriseDAO? = null
        val criseDataView = CrisesActivity.CriseDataViewObject()
        private val listaTodasCrises: ArrayList<Crise> = arrayListOf()
    
        fun getDAO(context: Context): CriseDAO {
            if (mDAO == null) {
                mDAO = CriseDAO(context)
            }
            return mDAO!!
        }
    
        fun obterCrises(context: Context) {
            listaTodasCrises.clear()
            listaTodasCrises.addAll(getDAO(context).getTodasCrises())
            criseDataView.crises.addAll(listaTodasCrises)
            criseDataView.crises.sortedByDescending { it.data }
            criseDataView.filtroAnos = 0
        }
        
        fun addCrise(crise: Crise) {
            listaTodasCrises.add(crise)
            filtrarCrises()
        }
    
        fun removerCrise(crise: Crise) {
            listaTodasCrises.remove(crise)
            filtrarCrises()
        }
    
        fun filtrarCrises() {
            with(criseDataView) {
                crises.clear()
                crises.addAll(listaTodasCrises.filter { calcularFiltro(it.data!!) }
                    .sortedByDescending { it.data })
            }
        }
        
        private fun calcularFiltro(data: Long): Boolean {
            if (criseDataView.filtroAnos > 0) {
                val calendar = Utils.getCalendar()
                //Log.i("[CriseContext] | ", "calcularFiltro() - HOJE: ${calendar.timeInMillis.formattedDate()} ")
    
                calendar.roll(Calendar.YEAR, -criseDataView.filtroAnos)
                //Log.i("[CriseContext] | ", "calcularFiltro() - DEPOIS DO ROLL: ${calendar.timeInMillis.formattedDate()} ")
    
                return calendar.timeInMillis <= data
            }
            return true
        }
    
    }
}
