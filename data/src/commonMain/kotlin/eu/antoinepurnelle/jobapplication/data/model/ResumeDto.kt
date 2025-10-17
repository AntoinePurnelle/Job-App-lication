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

package eu.antoinepurnelle.jobapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResumeDto(
    val record: ResumeWrapperDto? = null,
) {
    @Serializable
    data class ResumeWrapperDto(
        val mainInfo: MainInfoDto? = null,
        val experiences: List<ExperienceDto> = emptyList(),
        val education: EducationDto? = null,
        val skills: List<SkillDto> = emptyList(),
    ) {
        @Serializable
        data class MainInfoDto(
            val name: String? = null,
            val headline: String? = null,
            val pictureUrl: String? = null,
            val location: String? = null,
            val dateOfBirth: String? = null,
            val phoneNumber: String? = null,
            val emailAddress: String? = null,
            val linkedIn: String? = null,
            val github: String? = null,
            val mainSkills: List<String> = emptyList(),
        )

        @Serializable
        data class ExperienceDto(
            val id: String? = null,
            val title: String? = null,
            val company: String? = null,
            val pictureUrl: String? = null,
            val startDate: String? = null,
            val endDate: String? = null,
            val positions: List<PositionDto> = emptyList(),
        ) {
            @Serializable
            data class PositionDto(
                val title: String? = null,
                val description: String? = null,
                val skills: List<String> = emptyList(),
            )
        }

        @Serializable
        data class EducationDto(
            val diplomas: List<DiplomaDto> = emptyList(),
            val courses: List<CourseDto> = emptyList(),
            val conferences: List<ConferenceDto> = emptyList(),
        ) {
            @Serializable
            data class DiplomaDto(
                val name: String? = null,
                val description: String? = null,
                val date: String? = null,
                val establishment: String? = null,
                val pictureUrl: String? = null,
            )

            @Serializable
            data class CourseDto(
                val name: String? = null,
                val date: String? = null,
                val organization: String? = null,
                val pictureUrl: String? = null,
            )

            @Serializable
            data class ConferenceDto(
                val name: String? = null,
                val date: String? = null,
                val pictureUrl: String? = null,
            )
        }

        @Serializable
        data class SkillDto(
            val id: String? = null,
            val name: String? = null,
            val pictureUrl: String? = null,
        )
    }
}
