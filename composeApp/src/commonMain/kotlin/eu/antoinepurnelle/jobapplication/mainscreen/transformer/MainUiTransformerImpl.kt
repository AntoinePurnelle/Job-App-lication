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
import eu.antoinepurnelle.jobapplication.domain.model.UiModel
import eu.antoinepurnelle.jobapplication.domain.transformer.MainUiTransformer
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import eu.antoinepurnelle.jobapplication.util.formatLocalDate
import eu.antoinepurnelle.ui.components.molecules.model.PillModel
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel

class MainUiTransformerImpl : MainUiTransformer {
    @Suppress("UNCHECKED_CAST")
    override fun <T : UiModel> transform(model: Resume): T = MainUiModel(
        header = MainUiModel.Header(
            name = model.mainInfo.name,
            headline = model.mainInfo.headline,
            pictureUrl = model.mainInfo.pictureUrl,
            location = model.mainInfo.location,
            dateOfBirth = model.mainInfo.dateOfBirth?.let { formatLocalDate(it) },
            phoneNumber = model.mainInfo.phoneNumber,
            emailAddress = model.mainInfo.emailAddress,
            linkedIn = model.mainInfo.linkedIn,
            github = model.mainInfo.github,
            mainSkills = model.mainInfo.mainSkills.map { skill ->
                PillModel(
                    label = skill.name,
                    iconUrl = skill.pictureUrl,
                )
            },
        ),
        experiences = model.experiences.map {
            SectionCardItemModel(
                id = it.id,
                title = it.title,
                subtitle = it.company,
                pictureUrl = it.pictureUrl,
                date = if (it.endDate != null) {
                    "${formatLocalDate(it.startDate)} - ${formatLocalDate(it.endDate!!)}"
                } else {
                    "Since ${formatLocalDate(it.startDate)}" // TODO i18n
                },
            )
        },
        projects = model.projects.map {
            SectionCardItemModel(
                title = it.name,
                subtitle = it.description,
                pictureUrl = it.pictureUrl,
                url = it.projectUrl,
                pills = it.skills.map { skill ->
                    PillModel(
                        label = skill.name,
                        iconUrl = skill.pictureUrl,
                    )
                },
            )
        },
        education = listOf(
            SubSectionModel(
                title = "Diplomas", // TODO i18n
                items = model.education.diplomas.map {
                    SectionCardItemModel(
                        title = it.name,
                        description = it.description,
                        date = it.dateObtained,
                        subtitle = it.establishment,
                        pictureUrl = it.pictureUrl,
                    )
                },
            ),
            SubSectionModel(
                title = "Courses", // TODO i18n
                items = model.education.courses.map {
                    SectionCardItemModel(
                        title = it.name,
                        date = it.dateCompleted,
                        subtitle = it.organization,
                        pictureUrl = it.pictureUrl,
                    )
                },
            ),
            SubSectionModel(
                title = "Conferences", // TODO i18n
                items = model.education.conferences.map {
                    SectionCardItemModel(
                        title = it.name,
                        date = it.dateAttended,
                        pictureUrl = it.pictureUrl,
                    )
                },
            ),
        ),
        other = model.other.map { item ->
            SectionCardItemModel(
                title = item,
            )
        },
    ) as T
}
