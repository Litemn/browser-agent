package com.opentool.playwright

import com.opentool.Keyboard
import com.opentool.Mouse
import com.opentool.Snapshot

class VisionPlaywrightAgent : Snapshot, Mouse, Keyboard {
    override fun getSnapshot(): String {
        TODO("Not yet implemented")
    }

    override fun click(x: Int, y: Int): String {
        TODO("Not yet implemented")
    }

    override fun typeText(text: String): String {
        TODO("Not yet implemented")
    }
}