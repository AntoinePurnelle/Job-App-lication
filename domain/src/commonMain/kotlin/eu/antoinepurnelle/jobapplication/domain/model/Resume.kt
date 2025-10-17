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

package eu.antoinepurnelle.jobapplication.domain.model

import kotlinx.datetime.LocalDate

data class Resume(
    val mainInfo: MainInfo,
    val experiences: List<Experience>,
    val education: Education,
) {
    data class MainInfo(
        val name: String,
        val headline: String,
        val pictureUrl: String?,
        val location: String?,
        val dateOfBirth: LocalDate?,
        val phoneNumber: String,
        val emailAddress: String,
        val linkedIn: String?,
        val github: String?,
        val mainSkills: List<Skill> = emptyList(),
    )

    data class Experience(
        val id: String,
        val title: String,
        val company: String,
        val pictureUrl: String?,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val positions: List<Position> = emptyList(),
    ) {
        data class Position(
            val title: String,
            val description: String,
            val skills: List<Skill> = emptyList(),
        )
    }

    data class Education(
        val diplomas: List<Diploma> = emptyList(),
        val courses: List<Course> = emptyList(),
        val conferences: List<Conference> = emptyList(),
    ) {
        data class Diploma(
            val name: String,
            val description: String?,
            val dateObtained: String,
            val establishment: String,
            val pictureUrl: String?,
        )

        data class Course(
            val name: String,
            val dateCompleted: String,
            val organization: String,
            val pictureUrl: String?,
        )

        data class Conference(
            val name: String,
            val dateAttended: String,
            val pictureUrl: String?,
        )
    }

    data class Skill(
        val name: String,
        val pictureUrl: String?,
    )
}
