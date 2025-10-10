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

package eu.antoinepurnelle.jobapplication.data.remote

import eu.antoinepurnelle.jobapplication.data.model.NetworkError
import eu.antoinepurnelle.jobapplication.data.model.RepoError
import eu.antoinepurnelle.jobapplication.data.model.RepoResult
import eu.antoinepurnelle.jobapplication.data.model.Resume
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.repository.ResumeRepository
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformer
import io.ktor.client.call.body
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class ResumeRemoteRepository(
    private val client: ApiClient,
    private val transformer: ResumeDtoTransformer,
) : ResumeRepository {

    override suspend fun getResume(): RepoResult<Resume, RepoError> = try {
        val response = client.getResume()
        when (response.status.value) {
            in 200..299 -> transformer.transform(response.body<ResumeDto>())
            401 -> RepoResult.Error(NetworkError.UNAUTHORIZED)
            408 -> RepoResult.Error(NetworkError.REQUEST_TIMEOUT)
            409 -> RepoResult.Error(NetworkError.CONFLICT)
            413 -> RepoResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            429 -> RepoResult.Error(NetworkError.TOO_MANY_REQUESTS)
            in 500..599 -> RepoResult.Error(NetworkError.SERVER_ERROR)
            else -> RepoResult.Error(NetworkError.UNKNOWN)
        }
    } catch (e: SerializationException) {
        println(e)
        RepoResult.Error(NetworkError.SERIALIZATION)
    } catch (e: IllegalArgumentException) {
        println(e)
        RepoResult.Error(NetworkError.UNKNOWN)
    } catch (_: IOException) {
        RepoResult.Error(NetworkError.UNKNOWN)
    }

}
