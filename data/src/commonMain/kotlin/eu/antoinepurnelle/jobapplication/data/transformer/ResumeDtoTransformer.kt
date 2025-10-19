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

package eu.antoinepurnelle.jobapplication.data.transformer

import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto.ResumeWrapperDto.SkillDto
import eu.antoinepurnelle.jobapplication.domain.model.Failure
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

fun interface ResumeDtoTransformer {

    /**
     * Transforms a [ResumeDto] into a [Resume].
     *
     * @param input The [ResumeDto] to transform.
     * @return A [Result] containing either the transformed [Resume] on success or a [Failure] on failure.
     */
    fun transform(input: ResumeDto): Result<Resume, Failure>

}

class ResumeDtoTransformerImpl : ResumeDtoTransformer {

    @Suppress("ReturnCount") // Allowing multiple return points for clarity in error handling
    override fun transform(
        input: ResumeDto,
    ): Result<Resume, Failure> {
        val record = input.record ?: return getError()

        val mainInfo = transformMainInfo(record) ?: return getError()
        val experiences = transformExperiences(record)
        val projects = transformProjects(record)
        val education = transformEducation(record)
        val other = record.other

        return Result.Success(Resume(mainInfo, experiences, projects, education, other))
    }

    private fun transformMainInfo(record: ResumeWrapperDto): MainInfo? {
        // Validate mainInfo and its required fields
        val mainInfoDto = record.mainInfo ?: return null
        val name = mainInfoDto.name ?: return null
        val headline = mainInfoDto.headline ?: return null
        val phoneNumber = mainInfoDto.phoneNumber ?: return null
        val email = mainInfoDto.emailAddress ?: return null

        return MainInfo(
            name = name,
            headline = headline,
            pictureUrl = mainInfoDto.pictureUrl,
            location = mainInfoDto.location,
            dateOfBirth = mainInfoDto.dateOfBirth?.let { LocalDateParser.parse(it) },
            phoneNumber = phoneNumber,
            emailAddress = email,
            linkedIn = mainInfoDto.linkedIn,
            github = mainInfoDto.github,
            mainSkills = transformSkills(mainInfoDto.mainSkills, record.skills),
        )
    }

    private fun transformExperiences(record: ResumeWrapperDto): List<Experience> = record.experiences.mapNotNull { expDto ->
        val id = expDto.id ?: return@mapNotNull null
        val title = expDto.title ?: return@mapNotNull null
        val company = expDto.company ?: return@mapNotNull null
        val startDate = expDto.startDate?.let { LocalDateParser.parse(it) } ?: return@mapNotNull null
        val positions = expDto.positions.mapNotNull { posDto ->
            val posTitle = posDto.title ?: return@mapNotNull null
            val description = posDto.description ?: return@mapNotNull null

            Position(
                title = posTitle,
                description = description,
                skills = transformSkills(posDto.skills, record.skills),
            )
        }

        Experience(
            id = id,
            title = title,
            company = company,
            pictureUrl = expDto.pictureUrl,
            startDate = startDate,
            endDate = expDto.endDate?.let { LocalDateParser.parse(it) },
            positions = positions,
        )
    }

    private fun transformProjects(record: ResumeWrapperDto): List<Project> = record.projects.mapNotNull { projDto ->
        val name = projDto.name ?: return@mapNotNull null
        val description = projDto.description ?: return@mapNotNull null

        Project(
            name = name,
            description = description,
            pictureUrl = projDto.pictureUrl,
            projectUrl = projDto.url,
            skills = transformSkills(projDto.skills, record.skills),
        )
    }

    private fun transformEducation(record: ResumeWrapperDto): Education {
        val diplomas = record.education?.diplomas?.mapNotNull { diplomaDto ->
            val name = diplomaDto.name ?: return@mapNotNull null
            val dateObtained = diplomaDto.date ?: return@mapNotNull null
            val establishment = diplomaDto.establishment ?: return@mapNotNull null

            Diploma(
                name = name,
                description = diplomaDto.description,
                dateObtained = dateObtained,
                establishment = establishment,
                pictureUrl = diplomaDto.pictureUrl,
            )
        } ?: emptyList()

        val courses = record.education?.courses?.mapNotNull { courseDto ->
            val name = courseDto.name ?: return@mapNotNull null
            val dateCompleted = courseDto.date ?: return@mapNotNull null
            val organization = courseDto.organization ?: return@mapNotNull null

            Course(
                name = name,
                dateCompleted = dateCompleted,
                organization = organization,
                pictureUrl = courseDto.pictureUrl,
            )
        } ?: emptyList()

        val conferences = record.education?.conferences?.mapNotNull { confDto ->
            val name = confDto.name ?: return@mapNotNull null
            val dateAttended = confDto.date ?: return@mapNotNull null

            Conference(
                name = name,
                dateAttended = dateAttended,
                pictureUrl = confDto.pictureUrl,
            )
        } ?: emptyList()

        return Education(diplomas, courses, conferences)
    }

    private fun transformSkills(
        ids: List<String>,
        skillsList: List<SkillDto>,
    ): List<Skill> = ids.map { id ->
        transformSkill(id, skillsList)
    }

    private fun transformSkill(id: String, skillsList: List<SkillDto>): Skill {
        val skillDto = skillsList.find { it.id == id }

        return Skill(
            name = skillDto?.name ?: id,
            pictureUrl = skillDto?.pictureUrl,
        )
    }

    private fun getError() = Result.Error(TransformationFailure)

}
