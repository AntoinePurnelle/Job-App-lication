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

import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.navigation.NavController
import eu.antoinepurnelle.shared.orFalse

class Pilot(
    private var navController: NavController,
) {

    fun navigateTo(route: Route) {
        if (navController.isSafe()) {
            try {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
            } catch (e: IllegalArgumentException) {
                // TODO logging
            }
        } else {
            // TODO logging
        }
    }

    fun back() {
        if (navController.isSafe()) {
            navController.popBackStack()
        } else {
            // TODO logging
        }
    }

    fun NavController.isSafe() = currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(CREATED).orFalse()

}
