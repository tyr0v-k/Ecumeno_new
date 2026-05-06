package com.ecumeno.ui.rosary

import androidx.lifecycle.ViewModel
import com.ecumeno.core.utils.models.enums.Confession
import com.ecumeno.core.utils.models.enums.MysteryType
import com.ecumeno.core.utils.models.enums.PrayerType
import com.ecumeno.data.local.preferences.PrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class RosaryViewModel(private val prefs: PrefsHelper) : ViewModel() {
    private lateinit var rosaryStructure: List<PrayerType>
    private var rosaryStart = 1
    private var rosaryLimit = 0
    private var currentDecade = 0
    private var currentBead = 0
    private var currentPrayerIndex = 0
    private val _uiState = MutableStateFlow(RosaryUiState())
    val uiState: StateFlow<RosaryUiState> = _uiState

    init {
        initRosaryStructure();
        updateDisplay()
    }

    fun nextBead() {
        if(currentDecade == rosaryLimit && currentPrayerIndex == rosaryStructure.size - 1){
            resetRosary()
        }
        else{
            if (Confession.fromPreferences(prefs.confession) == Confession.cat && prefs.isRuleEnabled){
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
        if (Confession.fromPreferences(prefs.confession) == Confession.cat && prefs.isRuleEnabled){
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
        if (prefs.isRuleEnabled){
            when (Confession.fromPreferences(prefs.confession)){
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
        val confession = Confession.fromPreferences(prefs.confession)
        val prayerType = rosaryStructure[currentPrayerIndex]
        val delimiter = prayerType != uiState.value.prayerType
        var displayBead = currentBead
        if (confession == Confession.lut && prefs.isRuleEnabled) displayBead++
        val mysteryVisibility = confession == Confession.cat && prefs.isRuleEnabled
        val mysteryType = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.TUESDAY -> MysteryType.SORROWFUL
            Calendar.FRIDAY -> MysteryType.SORROWFUL
            Calendar.WEDNESDAY -> MysteryType.GLORIOUS
            Calendar.SUNDAY -> MysteryType.GLORIOUS
            Calendar.THURSDAY -> MysteryType.LUMINOUS
            else -> MysteryType.JOYFUL
        }
        val prayerVisibility = prefs.isRuleEnabled
        val decadeVisibility = currentDecade != 0 && !(confession == Confession.lut && prefs.isRuleEnabled)
        val beadVisibility = displayBead != 0 && !(confession == Confession.cat && currentPrayerIndex > 16)
        val hasCenterPiece = confession == Confession.cat && currentPrayerIndex > 16
        val currBead = if (!hasCenterPiece && (confession != Confession.cat || (confession == Confession.cat && !prefs.isRuleEnabled) || (confession == Confession.cat && prefs.isRuleEnabled && currentPrayerIndex > 5))) currentBead + 10 else currentBead
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