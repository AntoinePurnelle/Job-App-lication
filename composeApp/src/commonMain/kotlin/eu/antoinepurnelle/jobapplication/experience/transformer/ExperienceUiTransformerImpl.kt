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

import eu.antoinepurnelle.jobapplication.domain.model.Resume.Experience
import eu.antoinepurnelle.jobapplication.domain.model.UiModel
import eu.antoinepurnelle.jobapplication.domain.transformer.ExperienceUiTransformer
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel.ExperienceHeader
import eu.antoinepurnelle.jobapplication.util.formatLocalDate
import eu.antoinepurnelle.ui.components.molecules.model.PillModel
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel

class ExperienceUiTransformerImpl : ExperienceUiTransformer {
    @Suppress("UNCHECKED_CAST")
    override fun <T : UiModel> transform(
        model: Experience,
    ): T = ExperienceUiModel(
        header = ExperienceHeader(
            title = model.title,
            company = model.company,
            pictureUrl = model.pictureUrl,
            date = if (model.endDate != null) {
                "${formatLocalDate(model.startDate)} - ${formatLocalDate(model.endDate!!)}"
            } else {
                "Since ${formatLocalDate(model.startDate)}" // TODO i18n
            },
        ),
        positions = model.positions.map { position ->
            SubSectionModel(
                title = position.title,
                items = listOf(
                    SectionCardItemModel(
                        title = position.description,
                        pills = position.skills.map { skill ->
                            PillModel(
                                label = skill.name,
                                iconUrl = skill.pictureUrl,
                            )
                        },
                    ),
                ),
            )
        },
    ) as T
}
