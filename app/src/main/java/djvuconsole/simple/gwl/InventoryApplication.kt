package djvuconsole.simple.gwl

import android.app.Application
import djvuconsole.simple.gwl.data.AppContainer
import djvuconsole.simple.gwl.data.AppDataContainer

class InventoryApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    internal lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}