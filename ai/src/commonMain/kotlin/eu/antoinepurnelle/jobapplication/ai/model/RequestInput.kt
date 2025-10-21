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

package eu.antoinepurnelle.jobapplication.ai.model

import eu.antoinepurnelle.jobapplication.ai.model.RequestInput.ContentInput.PartInput
import kotlinx.serialization.Serializable

@Serializable
data class RequestInput(
    val contents: List<ContentInput>,
) {
    constructor(text: String, resume: String) : this(
        contents = listOf(
            ContentInput(
                parts = listOfNotNull(
                    PartInput(text = text),
                    PartInput(text = "Resume:\n$resume"),
                    PartInput(text = "Answer in a Markdown format"),
                ),
            ),
        ),
    )

    @Serializable
    data class ContentInput(
        val parts: List<PartInput>,
    ) {
        @Serializable
        data class PartInput(
            val text: String,
        )
    }

}
