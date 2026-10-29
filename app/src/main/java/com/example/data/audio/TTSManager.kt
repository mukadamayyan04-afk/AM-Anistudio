package com.example.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _statusMessage = MutableStateFlow("TTS Ready")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale.US
                _statusMessage.value = "Voice Engine Online"
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _statusMessage.value = "Speech synthesis error"
                    }
                })
            } else {
                _statusMessage.value = "TTS Initialization Failed"
                Log.e("TTSManager", "TTS Init failed with status: $status")
            }
        }
    }

    fun speak(text: String, pitch: Float = 1.0f, speed: Float = 1.0f) {
        if (!isInitialized || tts == null) {
            _statusMessage.value = "Voice engine not ready"
            return
        }
        if (text.isBlank()) return

        tts?.setPitch(pitch)
        tts?.setSpeechRate(speed)
        val utteranceId = "AM_ANI_UTTER_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
