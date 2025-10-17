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

package eu.antoinepurnelle.jobapplication.mainscreen.model

import eu.antoinepurnelle.jobapplication.domain.model.UiModel
import eu.antoinepurnelle.ui.components.molecules.model.PillModel
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel

data class MainUiModel(
    val header: Header,
    val experiences: List<SectionCardItemModel> = emptyList(),
    val education: List<SubSectionModel> = emptyList(),
) : UiModel {
    data class Header(
        val name: String,
        val headline: String,
        val pictureUrl: String? = null,
        val location: String? = null,
        val dateOfBirth: String? = null,
        val phoneNumber: String,
        val emailAddress: String,
        val linkedIn: String? = null,
        val github: String? = null,
        val mainSkills: List<PillModel> = emptyList(),
    )
}
