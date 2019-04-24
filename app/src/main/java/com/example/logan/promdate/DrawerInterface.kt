package com.example.logan.promdate

import androidx.appcompat.widget.Toolbar

interface DrawerInterface {
    fun lockDrawer()
    fun unlockDrawer()
    fun setupDrawer(toolbar: Toolbar)
}