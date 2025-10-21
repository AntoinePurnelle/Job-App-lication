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
import eu.antoinepurnelle.jobapplication.domain.repository.AiRepository

fun interface PromptUseCase {
    /**
     * Sends a prompt to the AI and returns the response.
     *
     * @param prompt The prompt to send.
     * @return A [Result] containing the AI's response as a [String] on success,
     *         or a [Failure] on error.
     */
    suspend operator fun invoke(prompt: String): Result<String, Failure>
}

class PromptUseCaseImpl(
    private val aiRepository: AiRepository,
) : PromptUseCase {
    override suspend fun invoke(prompt: String): Result<String, Failure> = aiRepository.prompt(prompt)
}
