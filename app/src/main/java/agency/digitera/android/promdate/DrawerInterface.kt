package agency.digitera.android.promdate

import androidx.appcompat.widget.Toolbar

interface DrawerInterface {
    fun lockDrawer()
    fun unlockDrawer()
    fun setupDrawer(toolbar: Toolbar, currentLocation: Int)
}