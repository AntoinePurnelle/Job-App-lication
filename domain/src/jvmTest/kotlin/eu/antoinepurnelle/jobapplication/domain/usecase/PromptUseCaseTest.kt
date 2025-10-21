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
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PromptUseCaseTest {

    @MockK private lateinit var aiRepository: AiRepository

    private lateinit var useCase: PromptUseCase

    @BeforeTest
    fun setup() {
        aiRepository = mockk()

        useCase = PromptUseCaseImpl(aiRepository)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(aiRepository)
    }

    @Test
    fun `invoke - should call aiRepository prompt and return result`() = runTest {
        // GIVEN
        // THIS DATA
        val prompt = "Hello, AI!"
        val repoResult = mockk<Result<String, Failure>>()

        // THIS BEHAVIOR
        coEvery { aiRepository.prompt(prompt) } returns repoResult

        // WHEN
        val result = useCase(prompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            aiRepository.prompt(prompt)
        }

        // THIS SHOULD BE
        result shouldBe repoResult
    }

}
