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

package eu.antoinepurnelle.jobapplication.ai.remote

import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto
import eu.antoinepurnelle.jobapplication.ai.model.RequestInput
import eu.antoinepurnelle.jobapplication.ai.transformer.AiAnswerTransformer
import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.NetworkError
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.repository.AiRepository
import eu.antoinepurnelle.jobapplication.domain.repository.ResumeRepository
import io.ktor.client.call.body
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class GeminiRepository(
    private val client: GeminiApiClient,
    private val resumeRepository: ResumeRepository,
    private val transformer: AiAnswerTransformer,
) : AiRepository {

    override suspend fun prompt(prompt: String): Result<String, Failure> = when (val resumeResult = resumeRepository.getResume()) {
        is Result.Error -> Result.Error(resumeResult.error)
        is Result.Success -> promptWithResume(prompt, resumeResult.data)
    }

    /**
     * Sends a prompt along with the user's resume to the AI and returns the response.
     *
     * @param prompt The prompt to send.
     * @param resume The user's resume to include in the prompt.
     * @return A [Result] containing the AI's response as a [String] on success,
     *         or a [Failure] on error.
     */
    private suspend fun promptWithResume(prompt: String, resume: Resume): Result<String, Failure> = try {
        val response = client.prompt(RequestInput(prompt, Json.encodeToString(resume)))
        when (response.status.value) {
            in 200..299 -> {
                transformer.transform(response.body<AnswerDto>())
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            409 -> Result.Error(NetworkError.CONFLICT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    } catch (e: SerializationException) {
        println(e)
        Result.Error(NetworkError.SERIALIZATION)
    } catch (e: IllegalArgumentException) {
        println(e)
        Result.Error(NetworkError.UNKNOWN)
    } catch (_: IOException) {
        Result.Error(NetworkError.UNKNOWN)
    }

}
