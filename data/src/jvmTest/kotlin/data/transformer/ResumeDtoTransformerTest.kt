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

package data.transformer

import eu.antoinepurnelle.jobapplication.data.model.RepoResult
import eu.antoinepurnelle.jobapplication.data.model.Resume
import eu.antoinepurnelle.jobapplication.data.model.Resume.MainInfo
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.MainInfoDto
import eu.antoinepurnelle.jobapplication.data.model.TransformationError
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformer
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformerImpl
import io.kotest.matchers.shouldBe
import java.util.Random
import net.datafaker.Faker
import kotlin.test.BeforeTest
import kotlin.test.Test

class ResumeDtoTransformerTest {

    private lateinit var transformer: ResumeDtoTransformer

    @BeforeTest
    fun setup() {
        transformer = ResumeDtoTransformerImpl()
    }

    @Test
    fun `transform - invalid main info - should return Error`() {
        // GIVEN
        // THIS DATA
        val inputs = arrayOf(
            getDto(mainInfo = null),
            getDto(mainInfo = MainInfoDto()),
            getDto(mainInfo = getMainInfoDto(hasName = false)),
            getDto(mainInfo = getMainInfoDto(hasHeadline = false)),
            getDto(mainInfo = getMainInfoDto(hasPhoneNumber = false)),
            getDto(mainInfo = getMainInfoDto(hasEmailAddress = false)),
        )
        val expected = (0..inputs.lastIndex).map { RepoResult.Error(TransformationError) }

        // WHEN
        val results = inputs.map { input -> transformer.transform(input) }

        // THEN
        results shouldBe expected
    }

    @Test
    fun `transform - valid JSON - should return Success`() {
        // GIVEN
        // THIS DATA
        val input = getDto()
        val expected = RepoResult.Success(resume)

        // WHEN
        val result = transformer.transform(input)

        // THEN
        result shouldBe expected
    }

    private val faker = Faker(Random(0))

    private val name = faker.name().fullName()
    private val headline = faker.job().title()
    private val phoneNumber = faker.phoneNumber().phoneNumber()
    private val emailAddress = faker.internet().emailAddress()
    private val pictureUrl = faker.internet().url()
    private val location = faker.address().city()
    private val dateOfBirth = faker.timeAndDate().birthday(1, 99, "yyyy-MM-dd")
    private val linkedIn = faker.internet().url()
    private val github = faker.internet().url()

    private fun getDto(
        mainInfo: MainInfoDto? = getMainInfoDto(),
    ) = ResumeDto(
        record = ResumeWrapperDto(
            mainInfo = mainInfo,
        ),
    )

    private fun getMainInfoDto(
        hasName: Boolean = true,
        hasHeadline: Boolean = true,
        hasPhoneNumber: Boolean = true,
        hasEmailAddress: Boolean = true,
    ) = MainInfoDto(
        name = name.takeIf { hasName },
        headline = headline.takeIf { hasHeadline },
        pictureUrl = pictureUrl,
        location = location,
        dateOfBirth = dateOfBirth,
        phoneNumber = phoneNumber.takeIf { hasPhoneNumber },
        emailAddress = emailAddress.takeIf { hasEmailAddress },
        linkedIn = linkedIn,
        github = github,
    )

    private val mainInfo = MainInfo(
        name = name,
        headline = headline,
        pictureUrl = pictureUrl,
        location = location,
        dateOfBirth = dateOfBirth,
        phoneNumber = phoneNumber,
        emailAddress = emailAddress,
        linkedIn = linkedIn,
        github = github,
    )

    private val resume = Resume(mainInfo)

}
