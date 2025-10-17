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

package eu.antoinepurnelle.jobapplication.experience.transformer

import eu.antoinepurnelle.jobapplication.FakeData
import eu.antoinepurnelle.jobapplication.FakeData.exp1EndDateString
import eu.antoinepurnelle.jobapplication.FakeData.exp1StartDateString
import eu.antoinepurnelle.jobapplication.FakeData.experiences
import eu.antoinepurnelle.jobapplication.FakeData.skill1Name
import eu.antoinepurnelle.jobapplication.FakeData.skill1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.skill2Name
import eu.antoinepurnelle.jobapplication.FakeData.skill2PictureUrl
import eu.antoinepurnelle.jobapplication.domain.transformer.ExperienceUiTransformer
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel.ExperienceHeader
import eu.antoinepurnelle.jobapplication.util.formatLocalDate
import eu.antoinepurnelle.ui.components.molecules.model.PillModel
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ExperienceUiTransformerTest {

    private lateinit var transformer: ExperienceUiTransformer

    @BeforeTest
    fun setup() {
        mockkStatic("eu.antoinepurnelle.jobapplication.util.FormatLocalDateKt")

        transformer = ExperienceUiTransformerImpl()
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `transform - given an experience - should return the corresponding ui model`() {
        // GIVEN
        // THIS DATA
        val experience = experiences.first()
        val expected = uiModel

        // THIS BEHAVIOR
        every { formatLocalDate(experience.startDate) } returns exp1StartDateString
        every { formatLocalDate(experience.endDate!!) } returns exp1EndDateString

        // WHEN
        val result: ExperienceUiModel = transformer.transform(experience)

        // THEN
        // THIS SHOULD BE
        result shouldBe expected
    }

    private val uiModel = ExperienceUiModel(
        header = ExperienceHeader(
            title = FakeData.exp1Title,
            company = FakeData.exp1Company,
            pictureUrl = FakeData.exp1PictureUrl,
            date = "$exp1StartDateString - $exp1EndDateString",
        ),
        positions = listOf(
            SubSectionModel(
                title = FakeData.exp1Pos1Title,
                items = listOf(
                    SectionCardItemModel(
                        title = FakeData.exp1Pos1Description,
                        pills = listOf(
                            PillModel(
                                label = skill1Name,
                                iconUrl = skill1PictureUrl,
                            ),
                            PillModel(
                                label = skill2Name,
                                iconUrl = skill2PictureUrl,
                            ),
                        ),
                    ),
                ),
            ),
            SubSectionModel(
                title = FakeData.exp1Pos2Title,
                items = listOf(
                    SectionCardItemModel(
                        title = FakeData.exp1Pos2Description,
                        pills = emptyList(),
                    ),
                ),
            ),
        ),
    )

}
