/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package eu.antoinepurnelle.jobapplication

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.antoinepurnelle.jobapplication.Route.ExperienceDetailRoute
import eu.antoinepurnelle.jobapplication.Route.MainScreenRoute
import eu.antoinepurnelle.jobapplication.di.allModules
import eu.antoinepurnelle.jobapplication.experience.ExperienceScreen
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreen
import eu.antoinepurnelle.ui.components.atoms.gradientBackground
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

@Composable
@Preview
fun App() = KoinApplication(
    application = {
        modules(allModules)
    },
) {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize().gradientBackground(),
        ) {
            val navController = rememberNavController()
            val pilot = Pilot(navController)
            NavHost(
                navController = navController,
                startDestination = MainScreenRoute,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300),
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300),
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(300),
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300),
                    )
                },
            ) {
                composable<MainScreenRoute> {
                    MainScreen(pilot)
                }
                routeComposable<ExperienceDetailRoute> { route ->
                    ExperienceScreen(route.experienceId, pilot)
                }
            }
        }
    }
}

private inline fun <reified T : Route> NavGraphBuilder.routeComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(T) -> Unit,
) = composable<T>(
    typeMap = typeMap,
    content = { navBackStackEntry ->
        content(navBackStackEntry.toRoute<T>())
    },
)
