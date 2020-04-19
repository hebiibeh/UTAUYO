package com.example.utauyo

import android.app.Activity
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.OnRecordPositionUpdateListener
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import com.example.utauyo.logic.MyWaveFile
import java.io.File


class MainActivity : Activity() {

    private var waveFile: File = File("/voiceFile.wav")
    var audioRecord //録音用のオーディオレコードクラス
            : AudioRecord? = null
    val SAMPLING_RATE = 44100 //オーディオレコード用サンプリング周波数

    private var bufSize //オーディオレコード用バッファのサイズ
            = 0
    private lateinit var shortData //オーディオレコード用バッファ
            : ShortArray
    private val wav1 = MyWaveFile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sdFilePath = getSdCardFilesDirPathListForLollipop(this)

        initAudioRecord()

        initAudioRecordButton()
    }

    //AudioRecordの初期化
    private fun initAudioRecord() {
        wav1.createFile(SoundDefine.filePath)
        // AudioRecordオブジェクトを作成
        bufSize = AudioRecord.getMinBufferSize(
            SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufSize
        )
        shortData = ShortArray(bufSize / 2)

        // コールバックを指定
        audioRecord!!.setRecordPositionUpdateListener(object : OnRecordPositionUpdateListener {
            // フレームごとの処理
            override fun onPeriodicNotification(recorder: AudioRecord) {
                // TODO Auto-generated method stub
                audioRecord!!.read(shortData, 0, bufSize / 2) // 読み込む
                wav1.addBigEndianData(shortData) // ファイルに書き出す
            }

            override fun onMarkerReached(recorder: AudioRecord) {
                // TODO Auto-generated method stub
            }
        })
        // コールバックが呼ばれる間隔を指定
        audioRecord!!.positionNotificationPeriod = bufSize / 2
    }

    /**
     * オーディオレコード用のボタン初期化
     */
    private fun initAudioRecordButton() {
        // button event
        val btnIntent = findViewById<Button>(R.id.recVoiceBtn)
        btnIntent.setOnClickListener {
                startAudioRecord()
        }
        val btnIntent2 = findViewById<Button>(R.id.playVoiceBtn)
        btnIntent2.setOnClickListener {
                stopAudioRecord()
        }
//        playButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(view: View?) {
//                playRecord()
//            }
//        })
    }

    private fun startAudioRecord() {
        print("a")
        audioRecord!!.startRecording()
        audioRecord!!.read(shortData, 0, bufSize / 2)
    }

    //オーディオレコードを停止する
    private fun stopAudioRecord() {
        audioRecord!!.stop()
    }

    fun getSdCardFilesDirPathListForLollipop(context: Context): List<String>? {
        val sdCardFilesDirPathList: MutableList<String> = ArrayList()

        // getExternalFilesDirsはAndroid4.4から利用できるAPI。
        // filesディレクトリのリストを取得できる。
        val dirArr: Array<File> = context.getExternalFilesDirs(null)
        for (dir in dirArr) {
            if (dir != null) {
                val path = dir.absolutePath

                // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
                // 取り外し可能かどうか（SDカードかどうか）を判定している。
                if (Environment.isExternalStorageRemovable(dir)) {

                    // 取り外し可能であればSDカード。
                    if (!sdCardFilesDirPathList.contains(path)) {
                        sdCardFilesDirPathList.add(path)
                    }
                } else {
                    // 取り外し不可能であれば内部ストレージ。
                }
            }
        }
        return sdCardFilesDirPathList
    }

}
