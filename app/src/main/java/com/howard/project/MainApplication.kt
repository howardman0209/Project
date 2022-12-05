package com.howard.project

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

class MainApplication : Application(), ActivityLifecycleCallbacks {

    companion object {
//        inline fun <reified T : RoomDatabase> getDatabase(
//            context: Context,
//            databaseName: String,
//        ): T {
//            val dbBuilder = Room.databaseBuilder(context, T::class.java, databaseName).fallbackToDestructiveMigration()
//
//            /**
//             * For product DB, when version update cause Destructive Migration, clear saved checksum to force fetch product list again from host
//             */
//            if (databaseName == productDatabaseName) {
//                dbBuilder.addCallback(object : RoomDatabase.Callback() {
//                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
//                        super.onDestructiveMigration(db)
//                        Log.d("RoomDatabase", "product DB re-create clear checksum")
//                    }
//                })
//            }
//
//            return dbBuilder.build()
//        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(p0: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(p0: Activity) {
        TODO("Not yet implemented")
    }

}