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

package eu.antoinepurnelle.jobapplication.ai.transformer

import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto
import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto.CandidateDto
import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto.CandidateDto.ContentDto
import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto.CandidateDto.ContentDto.PartDto
import eu.antoinepurnelle.jobapplication.domain.model.Result
import io.kotest.matchers.shouldBe
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(JUnitParamsRunner::class)
class AiAnswerTransformerTest {

    private lateinit var transformer: AiAnswerTransformer

    @BeforeTest
    fun setup() {
        transformer = AiAnswerTransformerImpl()
    }

    @Test
    fun `transform - valid answer dto - returns success with text`() {
        // GIVEN
        // THIS DATA
        val answerText = "This is a test answer."
        val answerDto = AnswerDto(candidates = listOf(CandidateDto(content = ContentDto(parts = listOf(PartDto(answerText))))))
        val expected = Result.Success(answerText)

        // WHEN
        val result = transformer.transform(answerDto)

        // THEN
        // THIS SHOULD BE
        result shouldBe expected
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun parametersForTransform_invalidAnswerDto_returnsError() = listOf(
        // No candidates
        AnswerDto(candidates = emptyList()),
        // Candidate with no content
        AnswerDto(candidates = listOf(CandidateDto(content = ContentDto(parts = emptyList())))),
        // Content with no parts
        AnswerDto(candidates = listOf(CandidateDto(content = ContentDto(parts = emptyList())))),
        // Part with null text
        AnswerDto(candidates = listOf(CandidateDto(content = ContentDto(parts = listOf(PartDto(null)))))),
    )

    @Test
    @Parameters(method = "parametersForTransform_invalidAnswerDto_returnsError")
    fun `transform - invalid answer dto - returns error`(answerDto: AnswerDto) {
        // GIVEN
        // THIS DATA
        val expected = Result.Error(eu.antoinepurnelle.jobapplication.domain.model.TransformationFailure)

        // WHEN
        val result = transformer.transform(answerDto)

        // THEN
        // THIS SHOULD BE
        result shouldBe expected
    }

}
