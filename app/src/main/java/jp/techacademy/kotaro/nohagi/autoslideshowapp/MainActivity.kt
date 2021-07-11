package jp.techacademy.kotaro.nohagi.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import android.os.Looper
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    var count = 1
    private var mTimer: Timer? = null
    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    private var mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
                next_button.setOnClickListener { next() }
                back_button.setOnClickListener { before() }
                start_stop_button.setOnClickListener { auto() }
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
            next_button.setOnClickListener { next() }
            back_button.setOnClickListener { before() }
            start_stop_button.setOnClickListener { auto() }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                    next()
                    before()
                    auto()
                }
        }
    }
        private fun getContentsInfo() {
            // 画像の情報を取得する
            val resolver = contentResolver
            val cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                    null, // 項目（null = 全項目）
                    null, // フィルタ条件（null = フィルタなし）
                    null, // フィルタ用パラメータ
                    null // ソート (nullソートなし）
            )
            if(cursor!!.count > 0) {
                if (cursor.moveToFirst()) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    imageView.setImageURI(imageUri)
                }
                cursor.close()
            }
        }

    private fun next() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if(cursor!!.count > 0) {
            if (cursor.moveToFirst()) {
                if (count > cursor.count) count = 1
                for (i in 1 until count) {
                    cursor.moveToNext()
                }
                if (cursor.moveToNext()) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    count++
                } else {
                    cursor.moveToFirst()
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    count = 1
                }
            }
            cursor.close()
        }
    }

    private fun before() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if(cursor!!.count > 0) {
            if (cursor.moveToFirst()) {
                if (count > cursor.count) count = 1
                for (i in 1 until count) {
                    cursor.moveToNext()
                }

                if (cursor.moveToPrevious()) {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    count--
                } else {
                    cursor.moveToLast()
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                    count = cursor.count
                }
            }
            cursor.close()
        }
    }

    private fun auto() {
            if (start_stop_button.text == "再生") {
                start_stop_button.text = "停止"
                next_button.isEnabled = false
                back_button.isEnabled = false
                // タイマーの始動
                if (mTimer == null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 2.0
                            mHandler.post {
                                // 画像の情報を取得する
                                val resolver = contentResolver
                                val cursor = resolver.query(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                                    null, // 項目（null = 全項目）
                                    null, // フィルタ条件（null = フィルタなし）
                                    null, // フィルタ用パラメータ
                                    null // ソート (nullソートなし）
                                )
                                if (cursor!!.count > 0) {
                                    if (cursor.moveToFirst()) {
                                        if (count > cursor.count) count = 1
                                        for (i in 1 until count) {
                                            cursor.moveToNext()
                                        }

                                        if (cursor.moveToNext()) {
                                            // indexからIDを取得し、そのIDから画像のURIを取得する
                                            val fieldIndex =
                                                cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                            val id = cursor.getLong(fieldIndex)
                                            val imageUri = ContentUris.withAppendedId(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                id
                                            )
                                            imageView.setImageURI(imageUri)
                                            count++
                                        } else {
                                            cursor.moveToFirst()
                                            // indexからIDを取得し、そのIDから画像のURIを取得する
                                            val fieldIndex =
                                                cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                            val id = cursor.getLong(fieldIndex)
                                            val imageUri = ContentUris.withAppendedId(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                id
                                            )
                                            imageView.setImageURI(imageUri)
                                            count = 1
                                        }
                                    }
                                    cursor.close()
                                }
                            }
                        }
                    }, 2000, 2000)
                }
            } else {
                start_stop_button.text = "再生"
                mTimer!!.cancel()
                mTimer = null
                mTimerSec = 0.0
                next_button.isEnabled = true
                back_button.isEnabled = true
            }
    }
}
