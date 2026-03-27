package com.dominik.watchface

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.BatteryManager
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.Renderer
import java.time.ZonedDateTime
import java.util.Locale

class TagStyleWatchFaceService : WatchFaceService() {

    override fun createUserStyleSchema() = UserStyleSchema(emptyList())

    override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ) = ComplicationSlotsManager(emptyList(), currentUserStyleRepository)

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = TagStyleRenderer(
            context = applicationContext,
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            currentUserStyleRepository = currentUserStyleRepository
        )
        // Zurück zur stabilen Konstruktor-Signatur für Version 1.2.1
        return WatchFace(WatchFaceType.DIGITAL, renderer)
    }
}

private class TagStyleRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    currentUserStyleRepository: CurrentUserStyleRepository
) : Renderer.CanvasRenderer(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    CanvasType.SOFTWARE,
    16L,
    false
) {
    private val bgPaint = Paint().apply { color = Color.BLACK }
    private val timePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 96f
        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
    }
    private val datePaint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 30f
    }
    private val infoPaint = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
        textSize = 28f
    }
    private val linePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 2f
    }

    private val timeFormat = java.time.format.DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY)
    private val dateFormat = java.time.format.DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy", Locale.GERMANY)

    private val logo: Bitmap? by lazy {
        try {
            BitmapFactory.decodeResource(context.resources, R.drawable.omega_logo)
        } catch (e: Exception) {
            null
        }
    }

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()

        canvas.drawRect(0f, 0f, w, h, bgPaint)

        logo?.let {
            val targetW = (w * 0.40f).toInt()
            val scale = targetW.toFloat() / it.width
            val targetH = (it.height * scale).toInt()
            val dst = Rect(
                ((w - targetW) / 2f).toInt(),
                (h * 0.12f).toInt(),
                ((w + targetW) / 2f).toInt(),
                (h * 0.12f + targetH).toInt()
            )
            canvas.drawBitmap(it, null, dst, null)
        }

        val time = zonedDateTime.format(timeFormat)
        val date = zonedDateTime.format(dateFormat)

        canvas.drawText(time, w / 2f, h * 0.48f, timePaint)
        canvas.drawText(date, w / 2f, h * 0.56f, datePaint)

        val startY = h * 0.65f
        canvas.drawLine(w * 0.15f, startY - 10f, w * 0.85f, startY - 10f, linePaint)

        val battery = currentBatteryPercent(context)
        val items = listOf(
            "Akku: ${battery}%",
            "Omega Style"
        )

        items.forEachIndexed { i, text ->
            canvas.drawText(text, w * 0.20f, startY + (i * 40f) + 20f, infoPaint)
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) { }

    private fun currentBatteryPercent(ctx: Context): Int {
        val intent = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return -1
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return -1
        return (level * 100f / scale).toInt()
    }
}
