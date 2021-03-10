package se.oskarh.boardgamehub.repository

import android.content.Intent
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.oskarh.boardgamehub.util.IMPORT_COLLECTION_PERIOD_MS
import se.oskarh.boardgamehub.util.PROPERTY_COLLECTION_USERNAME
import se.oskarh.boardgamehub.util.extension.injector
import javax.inject.Inject

class ImportCollectionService : LifecycleService() {

    @Inject
    lateinit var importCollectionRepository: ImportCollectionRepository

    private var username: String = ""

    override fun onCreate() {
        super.onCreate()
        injector.inject(this)
        importCollectionRepository.importCollectionResponses.observe(this, { event ->
            event.takeIf { it.peekContent() is LoadingResponse }
                ?.getContentIfNotHandled()
                ?.let {
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(IMPORT_COLLECTION_PERIOD_MS)
                        importCollectionRepository.requestUserCollection(username)
                    }
                }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        username = intent?.getStringExtra(PROPERTY_COLLECTION_USERNAME).orEmpty()
        importCollectionRepository.requestUserCollection(username)
        return super.onStartCommand(intent, flags, startId)
    }
}