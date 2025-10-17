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

package eu.antoinepurnelle.jobapplication

import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Conference
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Course
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Diploma
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience.Position
import eu.antoinepurnelle.jobapplication.domain.model.Resume.MainInfo
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Skill
import io.mockk.mockk
import java.util.Random
import kotlinx.datetime.LocalDate
import net.datafaker.Faker

@Suppress("ConstPropertyName")
internal object FakeData {
    private val faker = Faker(Random(0))

    // MainInfo
    internal val name = faker.name().fullName()
    internal val headline = faker.job().title()
    internal val phoneNumber = faker.phoneNumber().phoneNumber()
    internal val emailAddress = faker.internet().emailAddress()
    internal val pictureUrl = faker.internet().url()
    internal val location = faker.address().city()
    internal val dateOfBirthString = faker.timeAndDate().birthday(1, 99, "yyyy-MM-dd")
    internal val dateOfBirthDate = mockk<LocalDate>()
    internal val linkedIn = faker.internet().url()
    internal val github = faker.internet().url()

    // Main Skills
    // Full -> OK
    internal val skill1Name = faker.job().keySkills()
    internal val skill1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    internal val skill2Name = faker.job().keySkills()
    internal val skill2PictureUrl: String? = null

    // Experience
    // Full -> OK
    internal const val exp1Id = "1"
    internal val exp1Title = faker.job().title()
    internal val exp1Company = faker.company().name()
    internal val exp1PictureUrl = faker.internet().url()
    internal const val exp1StartDateString = "2020-01-01"
    internal val exp1StartDate = mockk<LocalDate>()
    internal const val exp1EndDateString = "2022-01-01"
    internal val exp1EndDate = mockk<LocalDate>()
    internal val exp1Pos1Title = faker.job().title()
    internal val exp1Pos1Description = faker.lorem().paragraph()
    internal val exp1Pos2Title = faker.job().title()
    internal val exp1Pos2Description = faker.lorem().paragraph()

    // Necessary only -> OK
    internal const val exp2Id = "2"
    internal val exp2Title = faker.job().title()
    internal val exp2Company = faker.company().name()
    internal val exp2PictureUrl: String? = null
    internal const val exp2StartDateString = "2022-02-01"
    internal val exp2StartDate = mockk<LocalDate>()

    // Education
    // Diplomas
    // Full -> OK
    internal val dipl1Name = faker.educator().course()
    internal val dipl1Description = faker.lorem().sentence()
    internal const val dipl1Date = "2020"
    internal val dipl1Establishment = faker.university().name()
    internal val dipl1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    internal val dipl2Name = faker.educator().course()
    internal val dipl2Description: String? = null
    internal const val dipl2Date = "2021"
    internal val dipl2Establishment = faker.university().name()
    internal val dipl2PictureUrl: String? = null

    // Courses
    // Full -> OK
    internal val course1Name = faker.educator().course()
    internal const val course1Date = "2020"
    internal val course1Organization = faker.university().name()
    internal val course1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    internal val course2Name = faker.educator().course()
    internal const val course2Date = "2021"
    internal val course2Organization = faker.university().name()
    internal val course2PictureUrl: String? = null

    // Conferences
    // Full -> OK
    internal val conf1Name = faker.book().title()
    internal const val conf1Date = "2020"
    internal val conf1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    internal val conf2Name = faker.book().title()
    internal const val conf2Date = "2021"
    internal val conf2PictureUrl: String? = null

    internal fun getResume(
        mainInfo: MainInfo = this.mainInfo,
        experiences: List<Experience> = this.experiences,
        education: Education = this.education,
    ) = Resume(
        mainInfo = mainInfo,
        experiences = experiences,
        education = education,
    )

    internal val mainInfo = MainInfo(
        name = name,
        headline = headline,
        pictureUrl = pictureUrl,
        location = location,
        dateOfBirth = dateOfBirthDate,
        phoneNumber = phoneNumber,
        emailAddress = emailAddress,
        linkedIn = linkedIn,
        github = github,
        mainSkills = listOf(
            Skill(
                name = skill1Name,
                pictureUrl = skill1PictureUrl,
            ),
            Skill(
                name = skill2Name,
                pictureUrl = skill2PictureUrl,
            ),
        ),
    )

    internal val experiences = listOf(
        Experience(
            id = exp1Id,
            title = exp1Title,
            company = exp1Company,
            pictureUrl = exp1PictureUrl,
            startDate = exp1StartDate,
            endDate = exp1EndDate,
            positions = listOf(
                Position(
                    title = exp1Pos1Title,
                    description = exp1Pos1Description,
                    skills = listOf(
                        Skill(
                            name = skill1Name,
                            pictureUrl = skill1PictureUrl,
                        ),
                        Skill(
                            name = skill2Name,
                            pictureUrl = skill2PictureUrl,
                        ),
                    ),
                ),
                Position(
                    title = exp1Pos2Title,
                    description = exp1Pos2Description,
                    skills = emptyList(),
                ),
            ),
        ),
        Experience(
            id = exp2Id,
            title = exp2Title,
            company = exp2Company,
            pictureUrl = exp2PictureUrl,
            startDate = exp2StartDate,
            endDate = null,
        ),
    )

    internal val education = Education(
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
}
