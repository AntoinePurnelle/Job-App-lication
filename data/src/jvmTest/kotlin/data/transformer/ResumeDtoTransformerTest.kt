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

import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.EducationDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.EducationDto.ConferenceDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.EducationDto.CourseDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.EducationDto.DiplomaDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.ExperienceDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.ExperienceDto.PositionDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.MainInfoDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.ProjectDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.SkillDto
import eu.antoinepurnelle.jobapplication.data.transformer.LocalDateParser
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformer
import eu.antoinepurnelle.jobapplication.data.transformer.ResumeDtoTransformerImpl
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Conference
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Course
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Education.Diploma
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience.Position
import eu.antoinepurnelle.jobapplication.domain.model.Resume.MainInfo
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Project
import eu.antoinepurnelle.jobapplication.domain.model.Resume.Skill
import eu.antoinepurnelle.jobapplication.domain.model.TransformationFailure
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import java.util.Random
import kotlinx.datetime.LocalDate
import net.datafaker.Faker
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ResumeDtoTransformerTest {

    private lateinit var transformer: ResumeDtoTransformer

    @BeforeTest
    fun setup() {
        mockkObject(LocalDateParser)

        transformer = ResumeDtoTransformerImpl()
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
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
        val expected = (0..inputs.lastIndex).map { Result.Error(TransformationFailure) }

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
        val resume = getResume()
        val expected = Result.Success(resume)

        // THIS BEHAVIOR
        every { LocalDateParser.parse(dateOfBirthString) } returns dateOfBirthDate
        every { LocalDateParser.parse(exp1StartDateString) } returns exp1StartDate
        every { LocalDateParser.parse(exp1EndDateString) } returns exp1EndDate
        every { LocalDateParser.parse(exp2StartDateString) } returns exp2StartDate

        // WHEN
        val result = transformer.transform(input)

        // THEN
        result shouldBe expected
    }

    @Test
    fun `transform - valid JSON - empty experiences and education - should return Success with empty exp and edu`() {
        // GIVEN
        // THIS DATA
        val input = getDto(experiences = emptyList(), education = EducationDto())
        val resume = getResume(experiences = emptyList(), education = Education())
        val expected = Result.Success(resume)

        // THIS BEHAVIOR
        every { LocalDateParser.parse(dateOfBirthString) } returns dateOfBirthDate

        // WHEN
        val result = transformer.transform(input)

        // THEN
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

    // Main Skills
    // Full -> OK
    private val skill1Id = faker.idNumber().valid()
    private val skill1Name = faker.job().keySkills()
    private val skill1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    private val skill2Id = faker.idNumber().valid()
    private val skill2Name = faker.job().keySkills()
    private val skill2PictureUrl: String? = null

    // Missing name -> KO
    private val skill3Id = faker.job().keySkills()
    private val skill3Name: String? = null
    private val skill3PictureUrl = faker.internet().url()

    // Experience
    // Full -> OK
    private val exp1Id = "1"
    private val exp1Title = faker.job().title()
    private val exp1Company = faker.company().name()
    private val exp1PictureUrl = faker.internet().url()
    private val exp1StartDateString = "2020-01-01"
    private val exp1StartDate = mockk<LocalDate>()
    private val exp1EndDateString = "2022-01-01"
    private val exp1EndDate = mockk<LocalDate>()
    private val exp1Pos1Title = faker.job().title()
    private val exp1Pos1Description = faker.lorem().paragraph()
    private val exp1Pos2Title = faker.job().title()
    private val exp1Pos2Description = faker.lorem().paragraph()

    // Necessary only -> OK
    private val exp2Id = "2"
    private val exp2Title = faker.job().title()
    private val exp2Company = faker.company().name()
    private val exp2PictureUrl: String? = null
    private val exp2StartDateString = "2022-02-01"
    private val exp2StartDate = mockk<LocalDate>()
    private val exp2EndDateString: String? = null
    private val exp2Pos1Title = faker.job().title()
    private val exp2Pos1Description = null
    private val exp2Pos2Title = null
    private val exp2Pos2Description = faker.lorem().paragraph()

    // Missing title -> KO
    private val exp3Id = "3"
    private val exp3Title: String? = null
    private val exp3Company = faker.company().name()
    private val exp3PictureUrl: String? = null
    private val exp3StartDateString = "2022-02-01"
    private val exp3EndDateString: String? = null

    // Missing company -> KO
    private val exp4Id = "4"
    private val exp4Title = faker.job().title()
    private val exp4Company: String? = null
    private val exp4PictureUrl: String? = null
    private val exp4StartDateString = "2022-02-01"
    private val exp4EndDateString: String? = null

    // Missing start date -> KO
    private val exp5Id = "5"
    private val exp5Title = faker.job().title()
    private val exp5Company = faker.company().name()
    private val exp5PictureUrl: String? = null
    private val exp5StartDateString: String? = null
    private val exp5EndDateString: String? = null

    // Missing ID -> KO
    private val exp6Id: String? = null
    private val exp6Title = faker.job().title()
    private val exp6Company = faker.company().name()
    private val exp6PictureUrl: String? = null
    private val exp6StartDateString = "2022-02-01"
    private val exp6EndDateString: String? = null

    // Projects
    // Full -> OK
    private val proj1Name = faker.app().name()
    private val proj1Description = faker.lorem().paragraph()
    private val proj1Url = faker.internet().url()
    private val proj1PictureUrl = faker.internet().url()
    private val proj1Skills = listOf(skill1Id, skill2Id)

    // Necessary only -> OK
    private val proj2Name = faker.app().name()
    private val proj2Description = faker.lorem().paragraph()
    private val proj2Url: String? = null
    private val proj2PictureUrl: String? = null
    private val proj2Skills = emptyList<String>()

    // Missing name -> KO
    private val proj3Name: String? = null
    private val proj3Description = faker.lorem().paragraph()
    private val proj3Url = faker.internet().url()
    private val proj3PictureUrl = faker.internet().url()
    private val proj3Skills = listOf(skill1Id)

    // Missing description -> KO
    private val proj4Name = faker.app().name()
    private val proj4Description: String? = null
    private val proj4Url = faker.internet().url()
    private val proj4PictureUrl = faker.internet().url()
    private val proj4Skills = listOf(skill2Id)

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

    // Missing name -> KO
    private val dipl3Name: String? = null
    private val dipl3Description = faker.lorem().sentence()
    private val dipl3Date = "2022"
    private val dipl3Establishment = faker.university().name()
    private val dipl3PictureUrl = faker.internet().url()

    // Missing date -> KO
    private val dipl4Name = faker.educator().course()
    private val dipl4Description = faker.lorem().sentence()
    private val dipl4Date: String? = null
    private val dipl4Establishment = faker.university().name()

    // Missing establishment -> KO
    private val dipl5Name = faker.educator().course()
    private val dipl5Description = faker.lorem().sentence()
    private val dipl5Date = "2022"
    private val dipl5Establishment: String? = null
    private val dipl5PictureUrl = faker.internet().url()

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

    // Missing name -> KO
    private val course3Name: String? = null
    private val course3Date = "2022"
    private val course3Organization = faker.university().name()
    private val course3PictureUrl = faker.internet().url()

    // Missing date -> KO
    private val course4Name = faker.educator().course()
    private val course4Date: String? = null
    private val course4Organization = faker.university().name()
    private val course4PictureUrl = faker.internet().url()

    // Missing organization -> KO
    private val course5Name = faker.educator().course()
    private val course5Date = "2022"
    private val course5Organization: String? = null
    private val course5PictureUrl = faker.internet().url()

    // Conferences
    // Full -> OK
    private val conf1Name = faker.book().title()
    private val conf1Date = "2020"
    private val conf1PictureUrl = faker.internet().url()

    // Necessary only -> OK
    private val conf2Name = faker.book().title()
    private val conf2Date = "2021"
    private val conf2PictureUrl: String? = null

    // Missing name -> KO
    private val conf3Name: String? = null
    private val conf3Date = "2022"
    private val conf3PictureUrl = faker.internet().url()

    // Missing date -> KO
    private val conf4Name = faker.book().title()
    private val conf4Date: String? = null
    private val conf4PictureUrl = faker.internet().url()

    // Other
    private val other1 = faker.lorem().sentence()
    private val other2 = faker.lorem().sentence()

    private fun getDto(
        mainInfo: MainInfoDto? = getMainInfoDto(),
        experiences: List<ExperienceDto> = experienceDtos,
        projects: List<ProjectDto> = projectDtos,
        education: EducationDto = educationDto,
    ) = ResumeDto(
        record = ResumeWrapperDto(
            mainInfo = mainInfo,
            experiences = experiences,
            projects = projects,
            education = education,
            skills = skillDtos,
            other = listOf(other1, other2),
        ),
    )

    private fun getResume(
        mainInfo: MainInfo = this.mainInfo,
        experiences: List<Experience> = this.experience,
        projects: List<Project> = this.projects,
        education: Education = this.education,
    ) = Resume(
        mainInfo = mainInfo,
        experiences = experiences,
        projects = projects,
        education = education,
        other = listOf(other1, other2),
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
        dateOfBirth = dateOfBirthString,
        phoneNumber = phoneNumber.takeIf { hasPhoneNumber },
        emailAddress = emailAddress.takeIf { hasEmailAddress },
        linkedIn = linkedIn,
        github = github,
        mainSkills = listOf(skill1Id, skill2Id, skill3Id),
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
        mainSkills = listOf(
            Skill(
                name = skill1Name,
                pictureUrl = skill1PictureUrl,
            ),
            Skill(
                name = skill2Name,
                pictureUrl = skill2PictureUrl,
            ),
            Skill(
                name = skill3Id,
                pictureUrl = skill3PictureUrl,
            ),
        ),
    )

    private val experienceDtos = listOf(
        ExperienceDto(
            id = exp1Id,
            title = exp1Title,
            company = exp1Company,
            pictureUrl = exp1PictureUrl,
            startDate = exp1StartDateString,
            endDate = exp1EndDateString,
            positions = listOf(
                PositionDto(
                    title = exp1Pos1Title,
                    description = exp1Pos1Description,
                    skills = listOf(skill1Id, skill2Id),
                ),
                PositionDto(
                    title = exp1Pos2Title,
                    description = exp1Pos2Description,
                    skills = emptyList(),
                ),
            ),
        ),
        ExperienceDto(
            id = exp2Id,
            title = exp2Title,
            company = exp2Company,
            pictureUrl = exp2PictureUrl,
            startDate = exp2StartDateString,
            endDate = exp2EndDateString,
            positions = listOf(
                PositionDto(
                    title = exp2Pos1Title,
                    description = exp2Pos1Description,
                    skills = listOf(skill1Id),
                ),
                PositionDto(
                    title = exp2Pos2Title,
                    description = exp2Pos2Description,
                    skills = listOf(skill1Id),
                ),
            ),
        ),
        ExperienceDto(
            id = exp3Id,
            title = exp3Title,
            company = exp3Company,
            pictureUrl = exp3PictureUrl,
            startDate = exp3StartDateString,
            endDate = exp3EndDateString,
        ),
        ExperienceDto(
            id = exp4Id,
            title = exp4Title,
            company = exp4Company,
            pictureUrl = exp4PictureUrl,
            startDate = exp4StartDateString,
            endDate = exp4EndDateString,
        ),
        ExperienceDto(
            id = exp5Id,
            title = exp5Title,
            company = exp5Company,
            pictureUrl = exp5PictureUrl,
            startDate = exp5StartDateString,
            endDate = exp5EndDateString,
        ),
        ExperienceDto(
            id = exp6Id,
            title = exp6Title,
            company = exp6Company,
            pictureUrl = exp6PictureUrl,
            startDate = exp6StartDateString,
            endDate = exp6EndDateString,
        ),
    )

    private val experience = listOf(
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
            positions = emptyList(),
        ),
    )

    private val projectDtos = listOf(
        ProjectDto(
            name = proj1Name,
            description = proj1Description,
            url = proj1Url,
            pictureUrl = proj1PictureUrl,
            skills = proj1Skills,
        ),
        ProjectDto(
            name = proj2Name,
            description = proj2Description,
            url = proj2Url,
            pictureUrl = proj2PictureUrl,
            skills = proj2Skills,
        ),
        ProjectDto(
            name = proj3Name,
            description = proj3Description,
            url = proj3Url,
            pictureUrl = proj3PictureUrl,
            skills = proj3Skills,
        ),
        ProjectDto(
            name = proj4Name,
            description = proj4Description,
            url = proj4Url,
            pictureUrl = proj4PictureUrl,
            skills = proj4Skills,
        ),
    )

    private val projects = listOf(
        Project(
            name = proj1Name,
            description = proj1Description,
            pictureUrl = proj1PictureUrl,
            projectUrl = proj1Url,
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
        Project(
            name = proj2Name,
            description = proj2Description,
            pictureUrl = proj2PictureUrl,
            projectUrl = proj2Url,
            skills = emptyList(),
        ),
    )

    private val educationDto = EducationDto(
        diplomas = listOf(
            DiplomaDto(
                name = dipl1Name,
                description = dipl1Description,
                date = dipl1Date,
                establishment = dipl1Establishment,
                pictureUrl = dipl1PictureUrl,
            ),
            DiplomaDto(
                name = dipl2Name,
                description = dipl2Description,
                date = dipl2Date,
                establishment = dipl2Establishment,
                pictureUrl = dipl2PictureUrl,
            ),
            DiplomaDto(
                name = dipl3Name,
                description = dipl3Description,
                date = dipl3Date,
                establishment = dipl3Establishment,
                pictureUrl = dipl3PictureUrl,
            ),
            DiplomaDto(
                name = dipl4Name,
                description = dipl4Description,
                date = dipl4Date,
                establishment = dipl4Establishment,
                pictureUrl = null,
            ),
            DiplomaDto(
                name = dipl5Name,
                description = dipl5Description,
                date = dipl5Date,
                establishment = dipl5Establishment,
                pictureUrl = dipl5PictureUrl,
            ),
        ),
        courses = listOf(
            CourseDto(
                name = course1Name,
                date = course1Date,
                organization = course1Organization,
                pictureUrl = course1PictureUrl,
            ),
            CourseDto(
                name = course2Name,
                date = course2Date,
                organization = course2Organization,
                pictureUrl = course2PictureUrl,
            ),
            CourseDto(
                name = course3Name,
                date = course3Date,
                organization = course3Organization,
                pictureUrl = course3PictureUrl,
            ),
            CourseDto(
                name = course4Name,
                date = course4Date,
                organization = course4Organization,
                pictureUrl = course4PictureUrl,
            ),
            CourseDto(
                name = course5Name,
                date = course5Date,
                organization = course5Organization,
                pictureUrl = course5PictureUrl,
            ),
        ),
        conferences = listOf(
            ConferenceDto(
                name = conf1Name,
                date = conf1Date,
                pictureUrl = conf1PictureUrl,
            ),
            ConferenceDto(
                name = conf2Name,
                date = conf2Date,
                pictureUrl = conf2PictureUrl,
            ),
            ConferenceDto(
                name = conf3Name,
                date = conf3Date,
                pictureUrl = conf3PictureUrl,
            ),
            ConferenceDto(
                name = conf4Name,
                date = conf4Date,
                pictureUrl = conf4PictureUrl,
            ),
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

    private val skillDtos = listOf(
        SkillDto(
            id = skill1Id,
            name = skill1Name,
            pictureUrl = skill1PictureUrl,
        ),
        SkillDto(
            id = skill2Id,
            name = skill2Name,
            pictureUrl = skill2PictureUrl,
        ),
        SkillDto(
            id = skill3Id,
            name = skill3Name,
            pictureUrl = skill3PictureUrl,
        ),
    )

}
