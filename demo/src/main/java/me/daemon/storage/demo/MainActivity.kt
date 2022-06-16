package me.daemon.storage.demo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import me.daemon.storage.FileMetadata
import me.daemon.storage.closeMedia
import me.daemon.storage.openFile
import me.daemon.storage.openFileDescriptor
import java.io.FileOutputStream
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener {
            thread {
                val uri = openFile(FileMetadata("abc.data")) ?: return@thread
                openFileDescriptor(uri)?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use {
                        it.write(1)
                        it.write(2)
                        it.write(3)
                    }
                }
                closeMedia(uri)
            }
        }
    }
}