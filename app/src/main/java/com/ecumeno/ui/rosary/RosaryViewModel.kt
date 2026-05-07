package com.ecumeno.ui.rosary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.data.local.preferences.Confession
import com.ecumeno.ui.rosary.enums.MysteryType
import com.ecumeno.ui.rosary.enums.PrayerType
import com.ecumeno.data.local.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RosaryViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    private lateinit var rosaryStructure: List<PrayerType>
    private var rosaryStart = 1
    private var rosaryLimit = 0
    private var currentDecade = 0
    private var currentBead = 0
    private var currentPrayerIndex = 0
    private val _uiState = MutableStateFlow(RosaryUiState())
    val uiState: StateFlow<RosaryUiState> = _uiState

    init {
        viewModelScope.launch {
            preferencesRepository.confession.collect { confession ->
                resetRosary()
            }
        }
    }

    fun nextBead() {
        if(currentDecade == rosaryLimit && currentPrayerIndex == rosaryStructure.size - 1){
            resetRosary()
        }
        else{
            if (Confession.fromPreferences(preferencesRepository.confession.value) == Confession.cat && preferencesRepository.isRuleEnabled.value){
                if (currentPrayerIndex < rosaryStructure.size - 1) {
                    if(currentPrayerIndex == rosaryLimit){
                        currentDecade++
                    }
                    currentPrayerIndex++
                }
                else{
                    currentPrayerIndex = rosaryLimit + 1
                    currentDecade++
                }
            } else{
                if (currentPrayerIndex < rosaryStructure.size - 1) {
                    currentPrayerIndex++
                }
                else{
                    currentDecade++
                    currentPrayerIndex = 0
                }
            }
            updateCurrentDecadeAndBead()
            updateDisplay()
        }
    }

    fun previousBead() {
        if (currentPrayerIndex > 0) {
            currentPrayerIndex--
            updateCurrentDecadeAndBead()
            updateDisplay()
        }
    }

    private fun updateCurrentDecadeAndBead() {
        if (Confession.fromPreferences(preferencesRepository.confession.value) == Confession.cat && preferencesRepository.isRuleEnabled.value){
            when {
                currentPrayerIndex == 0 -> { currentDecade = 0; currentBead = 0 }
                currentPrayerIndex <= 5 -> { currentDecade = 0; currentBead = currentPrayerIndex }
                else -> {
                    val beadInDecade = (currentPrayerIndex - 5) % 14
                    currentBead = beadInDecade
                }
            }
        }
        else{
            currentBead = currentPrayerIndex
        }
    }

    private fun initRosaryStructure(){
        if (preferencesRepository.isRuleEnabled.value){
            when (Confession.fromPreferences(preferencesRepository.confession.value)){
                Confession.cat -> {
                    rosaryStructure = listOf(
                        PrayerType.CREED,
                        PrayerType.OUR_FATHER,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.GLORY_BE,
                        PrayerType.OUR_FATHER,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.HAIL_MARY,
                        PrayerType.GLORY_BE,
                        PrayerType.FATIMA_PRAYER,
                    )
                    rosaryStart = 0
                    rosaryLimit = 5
                }
                Confession.lut -> {
                    rosaryStructure = listOf(
                        PrayerType.FRALSARKRANSEN_FIRST,
                        PrayerType.FRALSARKRANSEN_SECOND,
                        PrayerType.FRALSARKRANSEN_THIRD,
                        PrayerType.FRALSARKRANSEN_FOURTH,
                        PrayerType.FRALSARKRANSEN_SECOND,
                        PrayerType.FRALSARKRANSEN_FIFTH,
                        PrayerType.FRALSARKRANSEN_SECOND,
                        PrayerType.FRALSARKRANSEN_SIXTH,
                        PrayerType.FRALSARKRANSEN_SECOND,
                        PrayerType.FRALSARKRANSEN_SEVENTH,
                        PrayerType.FRALSARKRANSEN_EIGHT,
                        PrayerType.SILENCE,
                        PrayerType.SILENCE,
                        PrayerType.SILENCE,
                        PrayerType.FRALSARKRANSEN_NINTH,
                        PrayerType.FRALSARKRANSEN_SECOND,
                        PrayerType.FRALSARKRANSEN_TENTH,
                        PrayerType.FRALSARKRANSEN_SECOND
                    )
                    rosaryStart = 1
                    rosaryLimit = 0
                }
                Confession.ort -> {
                    rosaryStructure = listOf(
                        PrayerType.OUR_FATHER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER,
                        PrayerType.JESUS_PRAYER
                    )
                    rosaryStart = 1
                    rosaryLimit = 3
                }
            }
        }
        else {
            rosaryStructure = listOf(
                PrayerType.OUR_FATHER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER
            )
            rosaryStart = 1
            rosaryLimit = 3
        }
        currentDecade = rosaryStart
    }

    fun resetRosary() {
        initRosaryStructure()
        currentPrayerIndex = 0
        currentDecade = rosaryStart
        currentBead = 0
        updateDisplay()
    }

    private fun updateDisplay() {
        val confession = Confession.fromPreferences(preferencesRepository.confession.value)
        val prayerType = rosaryStructure[currentPrayerIndex]
        val delimiter = prayerType != uiState.value.prayerType
        var displayBead = currentBead
        if (confession == Confession.lut && preferencesRepository.isRuleEnabled.value) displayBead++
        val mysteryVisibility = confession == Confession.cat && preferencesRepository.isRuleEnabled.value
        val mysteryType = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.TUESDAY -> MysteryType.SORROWFUL
            Calendar.FRIDAY -> MysteryType.SORROWFUL
            Calendar.WEDNESDAY -> MysteryType.GLORIOUS
            Calendar.SUNDAY -> MysteryType.GLORIOUS
            Calendar.THURSDAY -> MysteryType.LUMINOUS
            else -> MysteryType.JOYFUL
        }
        val prayerVisibility = preferencesRepository.isRuleEnabled.value
        val decadeVisibility = currentDecade != 0 && !(confession == Confession.lut && preferencesRepository.isRuleEnabled.value)
        val beadVisibility = displayBead != 0 && !(confession == Confession.cat && currentPrayerIndex > 16)
        val hasCenterPiece = confession == Confession.cat && currentPrayerIndex > 16
        val currBead = if (!hasCenterPiece && (confession != Confession.cat || (confession == Confession.cat && !preferencesRepository.isRuleEnabled.value) || (confession == Confession.cat && preferencesRepository.isRuleEnabled.value && currentPrayerIndex > 5))) currentBead + 10 else currentBead
        _uiState.value = RosaryUiState(
            prayerType = prayerType,
            currentDecade = currentDecade,
            currentBead = currBead,
            displayBead = displayBead,
            mysteryType = mysteryType,
            mysteryVisibility = mysteryVisibility,
            prayerVisibility = prayerVisibility,
            decadeVisibility = decadeVisibility,
            beadVisibility = beadVisibility,
            hasCenterPiece = hasCenterPiece,
            isDelimiter = delimiter
        )
    }
}