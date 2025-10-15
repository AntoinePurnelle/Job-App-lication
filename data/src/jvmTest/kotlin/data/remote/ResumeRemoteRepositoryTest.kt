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

package data.remote

import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.remote.ApiClient
import eu.antoinepurnelle.jobapplication.data.remote.ResumeRemoteRepository
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformer
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
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class ResumeRemoteRepositoryTest {

    @MockK private lateinit var transformer: ResumeDtoTransformer
    @MockK private lateinit var client: ApiClient

    private lateinit var repository: ResumeRepository

    @BeforeTest
    fun setup() {
        client = mockk()
        transformer = mockk()

        repository = ResumeRemoteRepository(client, transformer)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(client, transformer)
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getTransformerResultParams() = arrayOf(
        arrayOf(Result.Error(TransformationFailure)),
        arrayOf(Result.Success(mockk<Resume>())),
    )

    @Test
    @Parameters(method = "getTransformerResultParams")
    fun `getResume - fetch success - should return transformer result`(
        transformerResult: Result<Resume, TransformationFailure>,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val httpResponse = mockk<HttpResponse>()
        val resumeDto = mockk<ResumeDto>()

        // THIS BEHAVIOR
        coEvery { client.getResume() } returns httpResponse
        coEvery { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<ResumeDto>() } returns resumeDto
        coEvery { transformer.transform(resumeDto) } returns transformerResult

        // WHEN
        val result = repository.getResume()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            client.getResume()
            transformer.transform(resumeDto)
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
    fun `getResume - fetch with invalid HTTP code - should return Error with proper NetworkError`(
        httpCode: Int,
        expectedError: NetworkError,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val httpResponse = mockk<HttpResponse>()
        val expected = Result.Error(expectedError)

        // THIS BEHAVIOR
        coEvery { client.getResume() } returns httpResponse
        coEvery { httpResponse.status } returns HttpStatusCode.fromValue(httpCode)

        // WHEN
        val result = repository.getResume()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify { client.getResume() }

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
    fun `getResume - fetch with client throwing - should return Error with proper NetworkError`(
        throwable: Throwable,
        expectedError: NetworkError,
    ) = runTest {
        // GIVEN
        // THIS DATA
        val expected = Result.Error(expectedError)

        // THIS BEHAVIOR
        coEvery { client.getResume() } throws throwable

        // WHEN
        val result = repository.getResume()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify { client.getResume() }

        // THIS SHOULD BE
        result shouldBe expected
    }

}
