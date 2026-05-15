/*
 * Copyright (C) 2025-2026 Vexzure
 * Copyright (C) 2026 Victoria Freeman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kin.athena.service.utils.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import androidx.core.graphics.drawable.IconCompat
import android.net.TrafficStats
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import java.util.Locale
import androidx.core.app.NotificationCompat
import com.kin.athena.R
import com.kin.athena.core.utils.NotificationUtils
import com.kin.athena.core.utils.NumberFormatter
import java.util.LinkedList
import java.util.Queue

class NetworkSpeedMonitorService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "network_speed_channel"
        private const val UPDATE_INTERVAL = 1000L
        private const val GRAPH_POINTS = 30
        
        const val ACTION_START = "com.kin.athena.START_NETWORK_SPEED_MONITOR"
        const val ACTION_STOP = "com.kin.athena.STOP_NETWORK_SPEED_MONITOR"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var notificationManager: NotificationManager? = null
    
    private var lastRxBytes = 0L
    private var lastTxBytes = 0L
    private var lastUpdateTime = 0L
    
    private val downloadSpeeds: Queue<Float> = LinkedList()
    private val uploadSpeeds: Queue<Float> = LinkedList()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        NotificationUtils.createNotificationChannel(
            this,
            CHANNEL_ID,
            R.string.notification_network_speed_channel,
            R.string.notification_network_speed_channel_desc,
            NotificationManager.IMPORTANCE_LOW
        )
        
        initializeTrafficStats()

        // Start foreground with proper service type for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(0f, 0f),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification(0f, 0f))
        }

        startMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Service is already started in onCreate
            }
            ACTION_STOP -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
    }

    private fun initializeTrafficStats() {
        lastRxBytes = TrafficStats.getTotalRxBytes()
        lastTxBytes = TrafficStats.getTotalTxBytes()
        lastUpdateTime = System.currentTimeMillis()
        
        // Initialize queues with 30 zero data points
        repeat(GRAPH_POINTS) {
            downloadSpeeds.offer(0f)
            uploadSpeeds.offer(0f)
        }
    }

    private fun startMonitoring() {
        updateRunnable = object : Runnable {
            override fun run() {
                updateNetworkSpeed()
                handler.postDelayed(this, UPDATE_INTERVAL)
            }
        }
        updateRunnable?.let { handler.post(it) }
    }

    private fun stopMonitoring() {
        updateRunnable?.let { handler.removeCallbacks(it) }
        updateRunnable = null
    }

    private fun updateNetworkSpeed() {
        val currentTime = System.currentTimeMillis()
        val currentRxBytes = TrafficStats.getTotalRxBytes()
        val currentTxBytes = TrafficStats.getTotalTxBytes()

        if (lastUpdateTime > 0) {
            val timeDiff = currentTime - lastUpdateTime
            if (timeDiff > 0) {
                val rxDiff = currentRxBytes - lastRxBytes
                val txDiff = currentTxBytes - lastTxBytes

                val downloadSpeed = (rxDiff * 1000f) / timeDiff
                val uploadSpeed = (txDiff * 1000f) / timeDiff

                updateSpeedQueues(downloadSpeed, uploadSpeed)
                updateNotification(downloadSpeed, uploadSpeed)
            }
        }

        lastRxBytes = currentRxBytes
        lastTxBytes = currentTxBytes
        lastUpdateTime = currentTime
    }

    private fun updateSpeedQueues(downloadSpeed: Float, uploadSpeed: Float) {
        downloadSpeeds.offer(downloadSpeed)
        uploadSpeeds.offer(uploadSpeed)

        while (downloadSpeeds.size > GRAPH_POINTS) {
            downloadSpeeds.poll()
        }
        while (uploadSpeeds.size > GRAPH_POINTS) {
            uploadSpeeds.poll()
        }
    }

    private fun updateNotification(downloadSpeed: Float, uploadSpeed: Float) {
        val notification = createNotification(downloadSpeed, uploadSpeed)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(downloadSpeed: Float, uploadSpeed: Float): Notification {
        val graphBitmap = createFullWidthGraph(downloadSpeed, uploadSpeed)

        val customView = RemoteViews(packageName, R.layout.notification_network_speed)
        customView.setImageViewBitmap(R.id.graph_image, graphBitmap)

        // Update text views with speed information
        val downloadText = formatSpeedSimple(downloadSpeed.toLong())
        val uploadText = formatSpeedSimple(uploadSpeed.toLong())

        // Create HTML text with colored arrows and grey speed text
        val downloadHtml = "<font color='#0000FF'>▼</font> <font color='#808080'>$downloadText</font>"
        val uploadHtml = "<font color='#FF0000'>▲</font> <font color='#808080'>$uploadText</font>"

        customView.setTextViewText(R.id.download_speed, android.text.Html.fromHtml(downloadHtml))
        customView.setTextViewText(R.id.upload_speed, android.text.Html.fromHtml(uploadHtml))

        // Create custom icon with speed text for status bar
        val statusBarIcon = createStatusBarIcon(formatSpeedForStatusBar(downloadSpeed.toLong()))

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(statusBarIcon)  // Custom icon with speed text
            .setCustomContentView(customView)
            .setCustomBigContentView(customView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
    }

    private fun createFullWidthGraph(currentDownload: Float, currentUpload: Float): Bitmap {
        val width = 800 // Wide enough for most notifications
        val height = 64 // Standard notification height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Transparent background
        canvas.drawColor(Color.TRANSPARENT)
        
        val downloadPaint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        
        val uploadPaint = Paint().apply {
            color = Color.RED
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val currentSpeeds = if (downloadSpeeds.isEmpty()) {
            listOf(0f, currentDownload)
        } else {
            downloadSpeeds.toList() + currentDownload
        }
        
        val currentUploadSpeeds = if (uploadSpeeds.isEmpty()) {
            listOf(0f, currentUpload)
        } else {
            uploadSpeeds.toList() + currentUpload
        }

        val maxSpeed = maxOf(
            currentSpeeds.maxOrNull() ?: 0f,
            currentUploadSpeeds.maxOrNull() ?: 0f,
            1f // Minimum to avoid division by zero
        )

        drawFullWidthSpeedGraph(canvas, currentSpeeds, downloadPaint, width, height, maxSpeed)
        drawFullWidthSpeedGraph(canvas, currentUploadSpeeds, uploadPaint, width, height, maxSpeed)

        


        return bitmap
    }

    private fun formatSpeedSimple(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B/s"
            bytes < 1024 * 1024 -> {
                val kb = bytes / 1024.0
                if (kb >= 10) "${kb.toInt()} KB/s" else "%.1f KB/s".format(Locale.US, kb).replace(".0", "")
            }
            bytes < 1024 * 1024 * 1024 -> {
                val mb = bytes / (1024.0 * 1024.0)
                if (mb >= 10) "${mb.toInt()} MB/s" else "%.1f MB/s".format(Locale.US, mb).replace(".0", "")
            }
            else -> {
                val gb = bytes / (1024.0 * 1024.0 * 1024.0)
                if (gb >= 10) "${gb.toInt()} GB/s" else "%.1f GB/s".format(Locale.US, gb).replace(".0", "")
            }
        }
    }

    private fun drawFullWidthSpeedGraph(canvas: Canvas, speeds: List<Float>, paint: Paint, width: Int, height: Int, maxSpeed: Float) {
        if (speeds.size < 2) return

        val path = Path()
        val pointCount = minOf(speeds.size, GRAPH_POINTS)
        val stepX = width.toFloat() / (pointCount - 1)
        val margin = height * 0.1f

        for (i in 0 until pointCount) {
            val speedIndex = maxOf(0, speeds.size - pointCount + i)
            val speed = speeds[speedIndex]
            val x = i * stepX
            val normalizedSpeed = speed / maxSpeed
            val y = height - margin - (normalizedSpeed * (height - 2 * margin))

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        canvas.drawPath(path, paint)
    }

    private fun formatSpeedForStatusBar(bytes: Long): String {
        return when {
            bytes < 1024 -> "${bytes}B"
            bytes < 1024 * 1024 -> {
                val kb = bytes / 1024.0
                if (kb >= 10) "${kb.toInt()}K" else "%.1fK".format(Locale.US, kb).replace(".0", "")
            }
            bytes < 1024 * 1024 * 1024 -> {
                val mb = bytes / (1024.0 * 1024.0)
                if (mb >= 10) "${mb.toInt()}M" else "%.1fM".format(Locale.US, mb).replace(".0", "")
            }
            else -> {
                val gb = bytes / (1024.0 * 1024.0 * 1024.0)
                if (gb >= 10) "${gb.toInt()}G" else "%.1fG".format(Locale.US, gb).replace(".0", "")
            }
        }
    }

    private fun createStatusBarIcon(speedText: String): IconCompat {
        // Create a bitmap to draw the speed text
        val width = 128
        val height = 128
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            // Adjust text size based on text length
            textSize = when {
                speedText.length <= 2 -> 80f
                speedText.length <= 3 -> 64f
                speedText.length <= 4 -> 52f
                else -> 42f
            }
        }

        // Draw text centered in bitmap
        val xPos = width / 2f
        val yPos = (height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(speedText, xPos, yPos, paint)

        return IconCompat.createWithBitmap(bitmap)
    }
}