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

package eu.antoinepurnelle.jobapplication.mainscreen.transformer

import eu.antoinepurnelle.jobapplication.FakeData.conf1Date
import eu.antoinepurnelle.jobapplication.FakeData.conf1Name
import eu.antoinepurnelle.jobapplication.FakeData.conf1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.conf2Date
import eu.antoinepurnelle.jobapplication.FakeData.conf2Name
import eu.antoinepurnelle.jobapplication.FakeData.conf2PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.course1Date
import eu.antoinepurnelle.jobapplication.FakeData.course1Name
import eu.antoinepurnelle.jobapplication.FakeData.course1Organization
import eu.antoinepurnelle.jobapplication.FakeData.course1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.course2Date
import eu.antoinepurnelle.jobapplication.FakeData.course2Name
import eu.antoinepurnelle.jobapplication.FakeData.course2Organization
import eu.antoinepurnelle.jobapplication.FakeData.course2PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.dateOfBirthDate
import eu.antoinepurnelle.jobapplication.FakeData.dateOfBirthString
import eu.antoinepurnelle.jobapplication.FakeData.dipl1Date
import eu.antoinepurnelle.jobapplication.FakeData.dipl1Description
import eu.antoinepurnelle.jobapplication.FakeData.dipl1Establishment
import eu.antoinepurnelle.jobapplication.FakeData.dipl1Name
import eu.antoinepurnelle.jobapplication.FakeData.dipl1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.dipl2Date
import eu.antoinepurnelle.jobapplication.FakeData.dipl2Description
import eu.antoinepurnelle.jobapplication.FakeData.dipl2Establishment
import eu.antoinepurnelle.jobapplication.FakeData.dipl2Name
import eu.antoinepurnelle.jobapplication.FakeData.dipl2PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.emailAddress
import eu.antoinepurnelle.jobapplication.FakeData.exp1Company
import eu.antoinepurnelle.jobapplication.FakeData.exp1EndDate
import eu.antoinepurnelle.jobapplication.FakeData.exp1EndDateString
import eu.antoinepurnelle.jobapplication.FakeData.exp1Id
import eu.antoinepurnelle.jobapplication.FakeData.exp1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.exp1StartDate
import eu.antoinepurnelle.jobapplication.FakeData.exp1StartDateString
import eu.antoinepurnelle.jobapplication.FakeData.exp1Title
import eu.antoinepurnelle.jobapplication.FakeData.exp2Company
import eu.antoinepurnelle.jobapplication.FakeData.exp2Id
import eu.antoinepurnelle.jobapplication.FakeData.exp2PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.exp2StartDate
import eu.antoinepurnelle.jobapplication.FakeData.exp2StartDateString
import eu.antoinepurnelle.jobapplication.FakeData.exp2Title
import eu.antoinepurnelle.jobapplication.FakeData.getResume
import eu.antoinepurnelle.jobapplication.FakeData.github
import eu.antoinepurnelle.jobapplication.FakeData.headline
import eu.antoinepurnelle.jobapplication.FakeData.linkedIn
import eu.antoinepurnelle.jobapplication.FakeData.location
import eu.antoinepurnelle.jobapplication.FakeData.name
import eu.antoinepurnelle.jobapplication.FakeData.phoneNumber
import eu.antoinepurnelle.jobapplication.FakeData.pictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.proj1Description
import eu.antoinepurnelle.jobapplication.FakeData.proj1Name
import eu.antoinepurnelle.jobapplication.FakeData.proj1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.proj1Url
import eu.antoinepurnelle.jobapplication.FakeData.proj2Description
import eu.antoinepurnelle.jobapplication.FakeData.proj2Name
import eu.antoinepurnelle.jobapplication.FakeData.skill1Name
import eu.antoinepurnelle.jobapplication.FakeData.skill1PictureUrl
import eu.antoinepurnelle.jobapplication.FakeData.skill2Name
import eu.antoinepurnelle.jobapplication.FakeData.skill2PictureUrl
import eu.antoinepurnelle.jobapplication.domain.transformer.MainUiTransformer
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
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

class MainPageUiTransformerTest {

    private lateinit var transformer: MainUiTransformer

    @BeforeTest
    fun setup() {
        mockkStatic("eu.antoinepurnelle.jobapplication.util.FormatLocalDateKt")

        transformer = MainUiTransformerImpl()
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `transform - given a resume - should return the corresponding ui model`() {
        // GIVEN
        // THIS DATA
        val resume = getResume()
        val expected = uiModel

        // THIS BEHAVIOR
        every { formatLocalDate(dateOfBirthDate) } returns dateOfBirthString
        every { formatLocalDate(exp1StartDate) } returns exp1StartDateString
        every { formatLocalDate(exp1EndDate) } returns exp1EndDateString
        every { formatLocalDate(exp2StartDate) } returns exp2StartDateString

        // WHEN
        val result: MainUiModel = transformer.transform(resume)

        // THEN
        // THIS SHOULD BE
        result shouldBe expected
    }

    val uiModel = MainUiModel(
        header = MainUiModel.Header(
            name = name,
            headline = headline,
            pictureUrl = pictureUrl,
            location = location,
            dateOfBirth = dateOfBirthString,
            phoneNumber = phoneNumber,
            emailAddress = emailAddress,
            linkedIn = linkedIn,
            github = github,
            mainSkills = listOf(
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
        experiences = listOf(
            SectionCardItemModel(
                id = exp1Id,
                title = exp1Title,
                subtitle = exp1Company,
                pictureUrl = exp1PictureUrl,
                date = "$exp1StartDateString - $exp1EndDateString",
            ),
            SectionCardItemModel(
                id = exp2Id,
                title = exp2Title,
                subtitle = exp2Company,
                pictureUrl = exp2PictureUrl,
                date = "Since $exp2StartDateString", // TODO i18n
            ),
        ),
        projects = listOf(
            SectionCardItemModel(
                title = proj1Name,
                subtitle = proj1Description,
                url = proj1Url,
                pictureUrl = proj1PictureUrl,
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
            SectionCardItemModel(
                title = proj2Name,
                subtitle = proj2Description,
            ),
        ),
        education = listOf(
            SubSectionModel(
                title = "Diplomas", // TODO i18n
                items = listOf(
                    SectionCardItemModel(
                        title = dipl1Name,
                        description = dipl1Description,
                        date = dipl1Date,
                        subtitle = dipl1Establishment,
                        pictureUrl = dipl1PictureUrl,
                    ),
                    SectionCardItemModel(
                        title = dipl2Name,
                        description = dipl2Description,
                        date = dipl2Date,
                        subtitle = dipl2Establishment,
                        pictureUrl = dipl2PictureUrl,
                    ),
                ),
            ),
            SubSectionModel(
                title = "Courses", // TODO i18n
                items = listOf(
                    SectionCardItemModel(
                        title = course1Name,
                        date = course1Date,
                        subtitle = course1Organization,
                        pictureUrl = course1PictureUrl,
                    ),
                    SectionCardItemModel(
                        title = course2Name,
                        date = course2Date,
                        subtitle = course2Organization,
                        pictureUrl = course2PictureUrl,
                    ),
                ),
            ),
            SubSectionModel(
                title = "Conferences", // TODO i18n
                items = listOf(
                    SectionCardItemModel(
                        title = conf1Name,
                        date = conf1Date,
                        pictureUrl = conf1PictureUrl,
                    ),
                    SectionCardItemModel(
                        title = conf2Name,
                        date = conf2Date,
                        pictureUrl = conf2PictureUrl,
                    ),
                ),
            ),
        ),
    )
}
