package io.bubble.core.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryMessageCodecTest {

    @Test
    fun encodeRequest_containsMacField() {
        val bytes = BatteryMessageCodec.encodeRequest("AA:BB:CC:DD:EE:FF")
        assertTrue(bytes.isNotEmpty())
        assertEquals(0x0A, bytes[0].toInt() and 0xFF)
    }

    @Test
    fun decodeResponse_roundTripFields() {
        val body = byteArrayOf(
            0x08, 0x4B, // percent = 75
            0x12, 0x11, // mac string len 17
            *"aa:bb:cc:dd:ee:ff".toByteArray(),
            0x18, 0x01, // charging = true
        )
        val info = BatteryMessageCodec.decodeResponse(body)
        assertNotNull(info)
        assertEquals(75, info!!.percent)
        assertEquals("AA:BB:CC:DD:EE:FF", info.mac)
        assertTrue(info.isCharging)
        assertTrue(info.isKnown)
    }

    @Test
    fun matchesBatteryResponse_sid1_cid8() {
        val frame = byteArrayOf(0x01, 0x08, 0x08, 0x32)
        assertTrue(BatteryMessageCodec.matchesBatteryResponse(frame))
    }
}
