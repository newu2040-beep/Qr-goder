package com.example.ui

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.DesignDao
import com.example.data.SavedDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

sealed class ObjectRecognizeState {
    object Idle : ObjectRecognizeState()
    object Loading : ObjectRecognizeState()
    data class Success(val recognizedText: String) : ObjectRecognizeState()
    data class Error(val message: String) : ObjectRecognizeState()
}

class MainViewModel(private val designDao: DesignDao) : ViewModel() {

    private val _recognizeState = MutableStateFlow<ObjectRecognizeState>(ObjectRecognizeState.Idle)
    val recognizeState: StateFlow<ObjectRecognizeState> = _recognizeState

    val savedDesigns = designDao.getAllDesigns()

    fun analyzeImageAndGenerateQrText(bitmap: Bitmap) {
        _recognizeState.value = ObjectRecognizeState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val base64Img = bitmap.toBase64()
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = "Identify the primary object in this image. Give me a concise and professional string of max 3-5 words describing it in a way that can be used as data in a QR code. For example, if it's a mug, return 'Coffee Mug'."),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Img))
                            )
                        )
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = "You are an expert image identification assistant. Keep responses short and factual and DO NOT ADD punctuation.")))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Unknown Object"
                _recognizeState.value = ObjectRecognizeState.Success(text)
            } catch (e: Exception) {
                e.printStackTrace()
                _recognizeState.value = ObjectRecognizeState.Error(e.message ?: "Analysis failed")
            }
        }
    }

    fun saveDesign(title: String, data: String, fgColor: Int, bgColor: Int, cornerType: String) {
        viewModelScope.launch {
            designDao.insertDesign(
                SavedDesign(
                    title = title,
                    data = data,
                    fgColor = fgColor,
                    bgColor = bgColor,
                    cornerType = cornerType,
                    logoUri = null
                )
            )
        }
    }
    
    fun deleteDesign(id: Int) {
        viewModelScope.launch {
            designDao.deleteDesignById(id)
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
