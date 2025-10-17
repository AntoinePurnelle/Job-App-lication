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

package eu.antoinepurnelle.jobapplication.domain.usecase

import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.UiModel
import eu.antoinepurnelle.jobapplication.domain.repository.ResumeRepository
import eu.antoinepurnelle.jobapplication.domain.transformer.ExperienceUiTransformer

interface GetExperiencePageUseCase {
    /**
     * Retrieves the experience data and transforms it into the desired UI model.
     *
     * @return A [Result] containing the transformed UI model on success, or a [Failure] on error.
     */
    suspend operator fun <T : UiModel> invoke(id: String): Result<T, Failure>
}

class GetExperiencePageUseCaseImpl(
    private val repository: ResumeRepository,
    private val transformer: ExperienceUiTransformer,
) : GetExperiencePageUseCase {
    override suspend fun <T : UiModel> invoke(
        id: String,
    ): Result<T, Failure> = when (val repoResult = repository.getExperienceById(id)) {
        is Result.Success -> {
            val uiModel: T = transformer.transform(repoResult.data)
            Result.Success(uiModel)
        }
        is Result.Error -> {
            Result.Error(repoResult.error)
        }
    }
}
