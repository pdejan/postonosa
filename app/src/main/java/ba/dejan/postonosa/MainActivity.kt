package ba.dejan.postonosa

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        val dao = db.racunDao()
        val prefs = getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val view = androidx.compose.ui.platform.LocalView.current
                    if (!view.isInEditMode) {
                        SideEffect {
                            val window = (view.context as android.app.Activity).window
                            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                        }
                    }
                    val sacuvaniId = prefs.getString("radnik_id", null)
                    val sacuvaniRejon = prefs.getString("radni_rejon", null)
                    val startDestinacija = if (sacuvaniId != null && sacuvaniRejon != null) "dashboard" else "login"
                    NavHost(navController = navController, startDestination = startDestinacija) {
                        composable("login") {
                            LoginEkran(navController, prefs)
                        }
                        composable("dashboard") {
                            DashboardEkran(navController, dao)
                        }
                        composable("unos") {
                            UnosEkran(navController, dao, prefs)
                        }
                        composable("lista") {
                            ListaEkran(navController, dao)
                        }
                        composable("kraj") {
                            KrajDanaEkran(navController, dao, prefs)
                        }
                        composable("podesavanja") {
                            PodesavanjaEkran(navController, prefs, dao)
                        }
                    }
                }
            }
        }
    }
}