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

import eu.antoinepurnelle.jobapplication.ai.model.RequestInput
import eu.antoinepurnelle.jobapplication.ai.remote.secrets.GeminiApiSecrets
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.timeout
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class GeminiApiClient {

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"

    private val client = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 180_000 // 3 minutes
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 180_000
        }
    }

    suspend fun prompt(
        input: RequestInput,
    ): HttpResponse {
        val response = client.post(baseUrl) {
            headers { append("x-goog-api-key", GeminiApiSecrets.API_KEY) }
            contentType(ContentType.Application.Json)
            setBody(input)
            timeout { requestTimeoutMillis = 120000L }
        }

        response.bodyAsText()

        return response
    }

}
