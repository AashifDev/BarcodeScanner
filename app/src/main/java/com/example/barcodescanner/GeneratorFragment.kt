package com.example.barcodescanner

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.Telephony.Mms.Addr.CHARSET
import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import com.example.barcodescanner.databinding.FragmentGeneratorBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import kotlin.coroutines.coroutineContext

class GeneratorFragment : Fragment() {
    lateinit var binding: FragmentGeneratorBinding
    var bitmap: Bitmap? = null
    val size = 150

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGeneratorBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressHorizontal.visibility = View.INVISIBLE
        showDialog()

    }
    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)

        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window?.attributes = lp

        val txt: AppCompatTextView = dialog.findViewById(R.id.textViewCode)
        val copy: AppCompatTextView = dialog.findViewById(R.id.textViewCopy)
        val close: AppCompatTextView = dialog.findViewById(R.id.textViewClose)
        val text: AppCompatEditText = dialog.findViewById(R.id.editTextWriteText)

        if (findNavController().currentDestination?.id == R.id.generatorFragment){
            txt.visibility = View.INVISIBLE
            copy.setText("Generate").toString()
        }

        copy.setOnClickListener {
            val text = text.text.toString().trim()
            if (!TextUtils.isEmpty(text)){
                //generateQrCode(text.text.toString())
                if (text.isNotEmpty()){
                    binding.progressHorizontal.visibility = View.VISIBLE
                    dialog.dismiss()
                    GlobalScope.launch(Dispatchers.Main) {
                        getQrCodeBitmap(text)
                    }
                }
            }
        }

        close.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }


        dialog.show()
    }

    private fun getQrCodeBitmap(ssid: String) {
        val size = 512 //pixels
        val bits = QRCodeWriter().encode(ssid, BarcodeFormat.QR_CODE, size, size)
        Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                    binding.imageViewQrCode.setImageBitmap(it)
                    binding.textViewCode.setText(ssid)
                    binding.progressHorizontal.visibility = View.GONE
                }
            }
        }
    }

}