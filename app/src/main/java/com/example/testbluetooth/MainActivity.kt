package com.example.testbluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        val PIN_CODE: String = "1234" // set your desired pin code here
        val UUID_STRING: String = "00001101-0000-1000-8000-00805F9B34FB" // UUID for serial port profile (SPP)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        var select_device_refresh2 = findViewById<Button>(R.id.select_device_refresh)
        select_device_refresh2.setOnClickListener{ pairedDeviceList() }
    }

    @SuppressLint("MissingPermission")
    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(this, "no paired bluetooth devices found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val select_device_list: ListView = findViewById(R.id.simpleListView)

        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val name: String = device.name
            Toast.makeText(this, "Connecting to $name", Toast.LENGTH_SHORT).show()
            connectToDevice(device)
        }
    }


    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        val uuid = UUID.fromString(UUID_STRING)
        val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
        try {
            socket.connect()
            Log.i("Bluetooth", "Connected to device ${device.name}")
            val outputStream: OutputStream = socket.outputStream
            outputStream.write(PIN_CODE.toByteArray())

            Log.i("Bluetooth", "Sent PIN code $PIN_CODE to device ${device.name}")
            Toast.makeText(this, "Sent PIN code $PIN_CODE  to device ${device.name}", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            Log.e("Bluetooth", "Error connecting to device ${device.name}: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                   // toast("Bluetooth has been enabled")
                } else {
                    //toast("Bluetooth has been disabled")
                }
            } else if (resultCode == RESULT_CANCELED) {
                //toast("Bluetooth enabling has been canceled")
            }
        }
    }
}