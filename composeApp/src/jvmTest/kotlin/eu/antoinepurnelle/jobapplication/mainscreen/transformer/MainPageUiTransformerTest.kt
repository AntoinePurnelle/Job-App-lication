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

import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Conference
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Course
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Diploma
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience
import eu.antoinepurnelle.jobapplication.domain.model.Resume.MainInfo
import eu.antoinepurnelle.jobapplication.domain.transformer.MainPageUiTransformer
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainPageUiModel
import eu.antoinepurnelle.jobapplication.util.formatLocalDate
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.util.Random
import kotlinx.datetime.LocalDate
import net.datafaker.Faker
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MainPageUiTransformerTest {

    private lateinit var transformer: MainPageUiTransformer

    @BeforeTest
    fun setup() {
        mockkStatic("eu.antoinepurnelle.jobapplication.util.FormatLocalDateKt")

        transformer = MainPageUiTransformerImpl()
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
        val result: MainPageUiModel = transformer.transform(resume)

        // THEN
        // THIS SHOULD BE
        result shouldBe expected
    }

    private val faker = Faker(Random(0))

    // MainInfo
    private val name = faker.name().fullName()
    private val headline = faker.job().title()
    private val phoneNumber = faker.phoneNumber().phoneNumber()
    private val emailAddress = faker.internet().emailAddress()
    private val pictureUrl = faker.internet().url()
    private val location = faker.address().city()
    private val dateOfBirthString = faker.timeAndDate().birthday(1, 99, "yyyy-MM-dd")
    private val dateOfBirthDate = mockk<LocalDate>()
    private val linkedIn = faker.internet().url()
    private val github = faker.internet().url()

    // Experience
    // Full -> OK
    private val exp1Title = faker.job().title()
    private val exp1Company = faker.company().name()
    private val exp1PictureUrl = faker.internet().url()
    private val exp1StartDateString = "2020-01-01"
    private val exp1StartDate = mockk<LocalDate>()
    private val exp1EndDateString = "2022-01-01"
    private val exp1EndDate = mockk<LocalDate>()

    // Necessary only -> OK
    private val exp2Title = faker.job().title()
    private val exp2Company = faker.company().name()
    private val exp2PictureUrl: String? = null
    private val exp2StartDateString = "2022-02-01"
    private val exp2StartDate = mockk<LocalDate>()

    // Education
    // Diplomas
    // Full -> OK
    private val dipl1Name = faker.educator().course()
    private val dipl1Description = faker.lorem().sentence()
    private val dipl1Date = "2020"
    private val dipl1Establishment = faker.university().name()
    private val dipl1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    private val dipl2Name = faker.educator().course()
    private val dipl2Description: String? = null
    private val dipl2Date = "2021"
    private val dipl2Establishment = faker.university().name()
    private val dipl2PictureUrl: String? = null

    // Courses
    // Full -> OK
    private val course1Name = faker.educator().course()
    private val course1Date = "2020"
    private val course1Organization = faker.university().name()
    private val course1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    private val course2Name = faker.educator().course()
    private val course2Date = "2021"
    private val course2Organization = faker.university().name()
    private val course2PictureUrl: String? = null

    // Conferences
    // Full -> OK
    private val conf1Name = faker.book().title()
    private val conf1Date = "2020"
    private val conf1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    private val conf2Name = faker.book().title()
    private val conf2Date = "2021"
    private val conf2PictureUrl: String? = null

    private fun getResume(
        mainInfo: MainInfo = this.mainInfo,
        experiences: List<Experience> = this.experience,
        education: Education = this.education,
    ) = Resume(
        mainInfo = mainInfo,
        experiences = experiences,
        education = education,
    )

    private val mainInfo = MainInfo(
        name = name,
        headline = headline,
        pictureUrl = pictureUrl,
        location = location,
        dateOfBirth = dateOfBirthDate,
        phoneNumber = phoneNumber,
        emailAddress = emailAddress,
        linkedIn = linkedIn,
        github = github,
    )

    private val experience = listOf(
        Experience(
            title = exp1Title,
            company = exp1Company,
            pictureUrl = exp1PictureUrl,
            startDate = exp1StartDate,
            endDate = exp1EndDate,
        ),
        Experience(
            title = exp2Title,
            company = exp2Company,
            pictureUrl = exp2PictureUrl,
            startDate = exp2StartDate,
            endDate = null,
        ),
    )

    private val education = Education(
        diplomas = listOf(
            Diploma(
                name = dipl1Name,
                description = dipl1Description,
                dateObtained = dipl1Date,
                establishment = dipl1Establishment,
                pictureUrl = dipl1PictureUrl,
            ),
            Diploma(
                name = dipl2Name,
                description = dipl2Description,
                dateObtained = dipl2Date,
                establishment = dipl2Establishment,
                pictureUrl = dipl2PictureUrl,
            ),
        ),
        courses = listOf(
            Course(
                name = course1Name,
                dateCompleted = course1Date,
                organization = course1Organization,
                pictureUrl = course1PictureUrl,
            ),
            Course(
                name = course2Name,
                dateCompleted = course2Date,
                organization = course2Organization,
                pictureUrl = course2PictureUrl,
            ),
        ),
        conferences = listOf(
            Conference(
                name = conf1Name,
                dateAttended = conf1Date,
                pictureUrl = conf1PictureUrl,
            ),
            Conference(
                name = conf2Name,
                dateAttended = conf2Date,
                pictureUrl = conf2PictureUrl,
            ),
        ),
    )

    val uiModel = MainPageUiModel(
        header = MainPageUiModel.Header(
            name = name,
            headline = headline,
            pictureUrl = pictureUrl,
            location = location,
            dateOfBirth = dateOfBirthString,
            phoneNumber = phoneNumber,
            emailAddress = emailAddress,
            linkedIn = linkedIn,
            github = github,
        ),
        experiences = listOf(
            SectionCardItemModel(
                title = exp1Title,
                subtitle = exp1Company,
                pictureUrl = exp1PictureUrl,
                date = "$exp1StartDateString - $exp1EndDateString",
            ),
            SectionCardItemModel(
                title = exp2Title,
                subtitle = exp2Company,
                pictureUrl = exp2PictureUrl,
                date = "Since $exp2StartDateString", // TODO i18n
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
