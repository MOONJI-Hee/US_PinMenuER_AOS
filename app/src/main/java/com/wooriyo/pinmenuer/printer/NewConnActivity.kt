package com.wooriyo.pinmenuer.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.broadcast.BtConnectReceiver
import com.wooriyo.pinmenuer.broadcast.BtDiscoveryReceiver
import com.wooriyo.pinmenuer.databinding.ActivityNewConnBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.printer.dialog.SetNickDialog
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.AppHelper.Companion.searchDevice
import java.io.IOException

class NewConnActivity : BaseActivity() {
    lateinit var binding: ActivityNewConnBinding

    val TAG = "NewConnActivity"
    val mActivity = this@NewConnActivity

    var printerNick = ""
    var printerModel = ""
    var printType = 0

    // Broadcast Receiver
    val connectDevice = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            val action = intent.action
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                binding.ivStatus.setImageResource(R.drawable.icon_print_connection_on)
                binding.tvStatus.setTextColor(Color.BLACK)
                binding.tvStatus.text = getString(R.string.after_conn)
                binding.tvNickPrint.setTextColor(Color.BLACK)
                binding.nickPrinter.isEnabled = true
                binding.btnRetry.visibility = View.GONE

                Toast.makeText(context, "BlueTooth Connect", Toast.LENGTH_SHORT).show()
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                try {
                    if (MyApplication.bluetoothPort.isConnected) MyApplication.bluetoothPort.disconnect()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (MyApplication.btThread != null) {
                    if(MyApplication.btThread!!.isAlive) {
                        MyApplication.btThread!!.interrupt()
                        MyApplication.btThread = null
                    }
                }
                binding.ivStatus.setImageResource(R.drawable.icon_print_connection_off)
                binding.tvStatus.setTextColor(Color.parseColor("#B4B4B4"))
                binding.tvStatus.text = getString(R.string.before_conn)
                binding.tvNickPrint.setTextColor(Color.parseColor("#B4B4B4"))
                binding.nickPrinter.isEnabled = false
                binding.btnRetry.visibility = View.VISIBLE
                Toast.makeText(context, "BlueTooth Disconnect", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val discoveryResult = BtDiscoveryReceiver()

    private val choosePrinterModel = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            val data = it.data ?: return@registerForActivityResult

            printType = data.getIntExtra("printType", 0)

            setPrintInfo()
            searchDevice()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BroadCast Receiver
        registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))
//        registerReceiver(searchStart, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
//        registerReceiver(searchFinish, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        binding.back.setOnClickListener { finish() }

        binding.nickDevice.setOnClickListener {
            showSetNickDialog(1)
        }
        binding.nickPrinter.setOnClickListener {
            showSetNickDialog(2)
        }
        binding.btnPlus.setOnClickListener {
            val intent = Intent(mActivity, SelectPrinterActivity::class.java)
            choosePrinterModel.launch(intent)
        }
        binding.btnRetry.setOnClickListener {
            val rtnVal = AppHelper.connDevice()

            if (rtnVal == 0) { // Connection success.
                val rh = RequestHandler()
                MyApplication.btThread = Thread(rh)
                MyApplication.btThread!!.start()
            } else // Connection failed.
                Toast.makeText(mActivity, "블루투스 연결 실패", Toast.LENGTH_SHORT).show()
        }
    }

    fun setPrintInfo() {
        var img = 0
        when(printType) {
            1 -> {
                printerModel = getString(R.string.skl_ts400b)
                img = R.drawable.skl_ts400b
            }
            2 -> {
                printerModel = getString(R.string.skl_te202)
                img = R.drawable.skl_te202
            }
            3 -> {
                printerModel = getString(R.string.sam4s)
                img = R.drawable.sam4s
            }
        }

        binding.ivPrinter.setImageResource(img)
        binding.btnPrinter.visibility = View.VISIBLE
        binding.btnPlus.visibility = View.INVISIBLE

        binding.printer.text = printerModel
        binding.tvNickPrint.setTextColor(Color.BLACK)
        binding.nickPrinter.isEnabled = true
    }

    fun showSetNickDialog(type: Int) {
        var nick = ""
        var model = ""
        var view = binding.nickDevice
        when(type) {
            1 -> {
                nick = ""
                model = "안드로이드 태블릿 PC"
            }
            2 -> {
                nick = printerNick
                model = printerModel
                view = binding.nickPrinter
            }
        }

        val nickDialog = SetNickDialog(mActivity, nick, type, model)
        nickDialog.setOnNickChangeListener(object : DialogListener{
            override fun onNickSet(nick: String) {
                view.text = nick
            }
        })
        nickDialog.show()
    }
}