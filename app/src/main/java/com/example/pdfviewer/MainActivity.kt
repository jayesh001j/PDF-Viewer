package com.example.pdfviewer

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfviewer.adapter.PdfAdapter
import com.example.pdfviewer.databinding.ActivityMainBinding
import com.example.pdfviewer.model.PdfModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val allPdfs = mutableListOf<PdfModel>()
    private val adapter by lazy {
        PdfAdapter(
            onItemClick = { openPdfFile(it.filePath) },
            onFavoriteClick = {
                it.isFavorite = !it.isFavorite
            }
        )
    }
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) loadAllPdfs()
        else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RVDATA.layoutManager = LinearLayoutManager(this)
        binding.RVDATA.adapter = adapter

        binding.Allpdfbtn.setOnClickListener {
            adapter.updateList(allPdfs)
        }

        binding.favbtn.setOnClickListener {
            val favList = allPdfs.filter { it.isFavorite }
            if (favList.isEmpty()) {
                Toast.makeText(this, "No favorites yet", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateList(favList)
            }
        }

        requestPermission()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            loadAllPdfs()
        }
    }

    private fun loadAllPdfs() {
        allPdfs.clear()
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFiles = downloads.listFiles { file -> file.extension.equals("pdf", true) }

        pdfFiles?.forEach {
            allPdfs.add(PdfModel(it.name, it.absolutePath))
        }

        adapter.updateList(allPdfs)
    }

    private fun openPdfFile(path: String) {
        try {
            val file = File(path)
            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
