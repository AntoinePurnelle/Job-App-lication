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

package eu.antoinepurnelle.jobapplication.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.antoinepurnelle.jobapplication.Pilot
import eu.antoinepurnelle.jobapplication.Route.ExperienceDetailRoute
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.usecase.FetchMainPageUseCase
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loading
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val pilot: Pilot,
    fetchMainPage: FetchMainPageUseCase,
) : ViewModel(), MainCallback {

    private val _uiState = MutableStateFlow<MainUiState>(Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = when (val result = fetchMainPage<MainUiModel>()) {
                is Result.Success -> {
                    MainUiState.Loaded(result.data)
                }
                is Result.Error -> {
                    MainUiState.Error(result.error.toString()) // TODO better handle errors
                }
            }
        }
    }

    override fun onExperienceClick(id: String) = pilot.navigateTo(ExperienceDetailRoute(id))

    sealed interface MainUiState {
        data object Loading : MainUiState
        data class Loaded(val data: MainUiModel) : MainUiState
        data class Error(val message: String) : MainUiState
    }

}

fun interface MainCallback {
    fun onExperienceClick(id: String)
}
