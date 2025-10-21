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
import eu.antoinepurnelle.jobapplication.domain.model.NetworkError
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.model.TransformationFailure
import eu.antoinepurnelle.jobapplication.domain.repository.ResumeRepository
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class GeminiRepositoryTest {

    @MockK private lateinit var client: GeminiApiClient
    @MockK private lateinit var resumeRepository: ResumeRepository
    @MockK private lateinit var transformer: AiAnswerTransformer

    private lateinit var repository: GeminiRepository

    private val aiAnswerText = "AI generated answer"

    @BeforeTest
    fun setup() {
        client = mockk()
        resumeRepository = mockk()
        transformer = mockk()

        mockkObject(Json.Default)

        repository = GeminiRepository(client, resumeRepository, transformer)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(client, resumeRepository, transformer)
        unmockkAll()
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getTransformerResultParams() = arrayOf(
        arrayOf(Result.Error(TransformationFailure)),
        arrayOf(Result.Success(aiAnswerText)),
    )

    @Test
    @Parameters(method = "getTransformerResultParams")
    fun `prompt - resume found - success - should call client and transformer and return transformer result`(
        transformerResult: Result<String, TransformationFailure>,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val prompt = "Test prompt"
        val resume = mockk<Resume>()
        val resumeResult = Result.Success(resume)
        val resumeString = "Serialized Resume"
        val input = RequestInput(prompt, resumeString)
        val httpResponse = mockk<HttpResponse>()
        val aiAnswerDto = mockk<AnswerDto>()

        // THIS BEHAVIOR
        coEvery { resumeRepository.getResume() } returns resumeResult
        every { Json.encodeToString(resume) } returns resumeString
        coEvery { client.prompt(input) } returns httpResponse
        coEvery { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<AnswerDto>() } returns aiAnswerDto
        every { transformer.transform(aiAnswerDto) } returns transformerResult

        // WHEN
        val result = repository.prompt(prompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerifySequence {
            resumeRepository.getResume()
            client.prompt(input)
            transformer.transform(aiAnswerDto)
        }

        // THIS SHOULD BE
        result shouldBe transformerResult
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getInvalidHttpCodesParams() = arrayOf(
        arrayOf<Any>(401, NetworkError.UNAUTHORIZED),
        arrayOf<Any>(408, NetworkError.REQUEST_TIMEOUT),
        arrayOf<Any>(409, NetworkError.CONFLICT),
        arrayOf<Any>(413, NetworkError.PAYLOAD_TOO_LARGE),
        arrayOf<Any>(429, NetworkError.TOO_MANY_REQUESTS),
        arrayOf<Any>(500, NetworkError.SERVER_ERROR),
    )

    @Test
    @Parameters(method = "getInvalidHttpCodesParams")
    fun `prompt - resume found - client returns error code - should return corresponding NetworkError`(
        httpCode: Int,
        expectedError: NetworkError,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val prompt = "Test prompt"
        val resume = mockk<Resume>()
        val resumeResult = Result.Success(resume)
        val resumeString = "Serialized Resume"
        val input = RequestInput(prompt, resumeString)
        val httpResponse = mockk<HttpResponse>()
        val expected = Result.Error(expectedError)

        // THIS BEHAVIOR
        coEvery { resumeRepository.getResume() } returns resumeResult
        every { Json.encodeToString(resume) } returns resumeString
        coEvery { client.prompt(input) } returns httpResponse
        coEvery { httpResponse.status } returns HttpStatusCode.fromValue(httpCode)

        // WHEN
        val result = repository.prompt(prompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            resumeRepository.getResume()
            client.prompt(input)
        }

        // THIS SHOULD BE
        result shouldBe expected
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getClientThrowableParams() = arrayOf(
        arrayOf(SerializationException(), NetworkError.SERIALIZATION),
        arrayOf(IllegalArgumentException(), NetworkError.UNKNOWN),
        arrayOf(IOException(), NetworkError.UNKNOWN),
    )

    @Test
    @Parameters(method = "getClientThrowableParams")
    fun `prompt - resume found - client throws exception - should return corresponding NetworkError`(
        thrownException: Throwable,
        expectedError: NetworkError,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val prompt = "Test prompt"
        val resume = mockk<Resume>()
        val resumeResult = Result.Success(resume)
        val resumeString = "Serialized Resume"
        val input = RequestInput(prompt, resumeString)
        val expected = Result.Error(expectedError)

        // THIS BEHAVIOR
        coEvery { resumeRepository.getResume() } returns resumeResult
        every { Json.encodeToString(resume) } returns resumeString
        coEvery { client.prompt(input) } throws thrownException

        // WHEN
        val result = repository.prompt(prompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            resumeRepository.getResume()
            client.prompt(input)
        }

        // THIS SHOULD BE
        result shouldBe expected
    }

    @Test
    fun `prompt - resume not found - should return resume repository error`() = runTest {
        // GIVEN
        // THIS DATA
        val prompt = "Test prompt"
        val expectedError = NetworkError.SERVER_ERROR
        val resumeResult = Result.Error(expectedError)
        val expected = Result.Error(expectedError)

        // THIS BEHAVIOR
        coEvery { resumeRepository.getResume() } returns resumeResult

        // WHEN
        val result = repository.prompt(prompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify { resumeRepository.getResume() }

        // THIS SHOULD BE
        result shouldBe expected
    }

}
