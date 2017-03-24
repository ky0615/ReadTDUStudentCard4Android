package moe.linux.boilerplate.api

import android.nfc.Tag
import android.nfc.tech.NfcF
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class NfcReadUtil {
    fun readTag(tag: Tag): StudentCard {
        val nfc = NfcF.get(tag)
        nfc.connect()
        val targetSystemCode = byteArrayOf(0xfe.toByte(), 0x00.toByte())
        val polling = nfc.transceive(polling(targetSystemCode))

        Timber.d("polling: " + polling.map(Byte::toInt).map { Integer.toHexString(it) }.joinToString(" "))

        // System 0 のIDｍを取得(1バイト目はデータサイズ、2バイト目はレスポンスコード、IDmのサイズは8バイト)
        val targetIDm = Arrays.copyOfRange(polling, 2, 10)
        Timber.d("targetIDm: " + targetIDm.map(Byte::toInt).map { Integer.toHexString(it) }.joinToString(" "))

        val targetServiceCode = byteArrayOf(0x8B.toByte(), 0x1A.toByte())
        val req = readWithoutEncryption(targetIDm, 4, targetServiceCode)
        Timber.d("req:   " + req.map(Byte::toInt).map { Integer.toHexString(it) }.joinToString(" "))

        val res = nfc.transceive(req)
        Timber.d("res: " + res.map(Byte::toInt).map { Integer.toHexString(it) }.joinToString(" "))

        nfc.close()
        return parse(res)
    }

    fun polling(systemCode: ByteArray): ByteArray {
        return ByteArrayOutputStream(100)
            .apply {
                write(0x00) // ダミー
                write(0x00) // コマンドコード
                write(byteArrayOf(systemCode[0])) // systemCode
                write(byteArrayOf(systemCode[1])) // systemCode
                write(0x01) // リクエストコード
                write(0x0f) // タイムスロット
            }
            .toByteArray()
            .apply { this[0] = size.toByte() }
    }

    fun readWithoutEncryption(idm: ByteArray, size: Int, serviceCode: ByteArray): ByteArray {
        return ByteArrayOutputStream(100)
            .apply {
                write(0x00) // ダミー
                write(0x06)
                write(idm)
                write(1)

                write(serviceCode)
                write(size)

                IntRange(0, size - 1).forEach {
                    write(0x80)
                    write(it)
                }
            }
            .toByteArray()
            .apply { this[0] = this.size.toByte() }
    }

    fun parse(res: ByteArray): StudentCard {
        // res[0] = データ長
        // res[1] = 0x07
        // res[2〜9] = カードID
        // res[10,11] = エラーコード, 0=正常
        // res[12] = 応答ブロック数
        // res[13+n*16] = 履歴データ。16byte/ブロックの繰り返し。

        if (res[10] != 0x00.toByte())
            throw RuntimeException("Read Without Encryption Command Error")

        val blockSize = res[12].toInt()

        val parse = IntRange(0, blockSize - 1).map {
            val offset = 13 + it * 16
            IntRange(0, 15).map { res[offset + it] }.toByteArray()
        }

        if (parse.size < 2)
            throw RuntimeException("block size is need upper 2")

        return StudentCard(
            number = String(parse[0]).substring(2, 9),
            name = String(parse[1].filter { it != 0x00.toByte() }.toByteArray())
        )
    }
}
