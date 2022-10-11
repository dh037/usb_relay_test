package com.example.usb_relay_test

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

// https://github.com/mik3y/usb-serial-for-android
// https://m.blog.naver.com/yuyyulee/221691984923
// https://heisanbug.tistory.com/3
// https://jitpack.io/p/kai-morich/usb-serial-for-android

class MainActivity : AppCompatActivity() {

    private val doorClose = byteArrayOf(0xA0.toByte(), 0x01.toByte(), 0x00.toByte(), 0xA1.toByte())
    private val doorOpen = byteArrayOf(0xA0.toByte(), 0x01.toByte(), 0x01.toByte(), 0xA2.toByte())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        // USB 모든 기기들 가져오기
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        var driver = availableDrivers[0]

        // 가져올 기기 찾기 테스트중인 기기는(29987) -> http://vctec.co.kr/product/1%EC%B1%84%EB%84%90-usb-%EB%A6%B4%EB%A0%88%EC%9D%B4-10a-1-channel-usb-relay-10a/14806/
        driver = availableDrivers.find {
            it.device.productId == 29987
        }


        val connection = manager.openDevice(driver.device)
            ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return

        val port = driver.ports[0] // Most devices have just one port (port 0)

        port.open(connection)
        // 기기 구매페이지에 파라미터가 명시되어 있음!
        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        findViewById<Button>(R.id.open).setOnClickListener {
            port.write(doorOpen, 500)
        }

        findViewById<Button>(R.id.close).setOnClickListener {
            port.write(doorClose, 500)
        }
    }
}