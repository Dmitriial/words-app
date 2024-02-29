package djvuconsole.simple.gwl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import djvuconsole.simple.gwl.data.InventoryDatabase
import djvuconsole.simple.gwl.ui.WordsViewDBModel
import djvuconsole.simple.gwl.ui.theme.GWLTheme


class MainActivity : ComponentActivity() {
    // get direct access to room database here (!)
    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = InventoryDatabase::class.java,
            name = "datamodel.db"
        ).allowMainThreadQueries().build()
    }
    private val databaseModel by viewModels<WordsViewDBModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WordsViewDBModel(db.wordDAO()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GWLTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(databaseModel = databaseModel, applicationContext = applicationContext)
                }
            }
        }
    }
}
