package fr.cornier.phonegg

import android.app.Application
import io.realm.Realm
import java.io.File

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Uncomment to reset the realm
        // resetRealm()

        Realm.init(this)
    }

    private fun resetRealm() {
        val file = File(this.filesDir, "default.realm")
        file.delete()
    }
}