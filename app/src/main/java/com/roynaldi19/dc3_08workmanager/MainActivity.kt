package com.roynaldi19.dc3_08workmanager

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.WorkManager
import com.roynaldi19.dc3_08workmanager.databinding.ActivityMainBinding
import androidx.work.Data
import androidx.work.OneTimeWorkRequest

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var workManager: WorkManager
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        workManager = WorkManager.getInstance(this)
        binding.btnOneTimeTask.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOneTimeTask -> startOneTimeTask()
        }
    }

    private fun startOneTimeTask() {
        binding.textStatus.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, binding.editCity.text.toString())
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .build()
        workManager.enqueue(oneTimeWorkRequest)
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this@MainActivity) { workInfo ->
                val status = workInfo.state.name
                binding.textStatus.append("\n" + status)
            }
    }
}