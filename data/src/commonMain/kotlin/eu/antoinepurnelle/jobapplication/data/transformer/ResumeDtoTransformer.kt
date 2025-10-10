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

import eu.antoinepurnelle.jobapplication.data.model.RepoError
import eu.antoinepurnelle.jobapplication.data.model.RepoResult
import eu.antoinepurnelle.jobapplication.data.model.Resume
import eu.antoinepurnelle.jobapplication.data.model.ResumeDto
import eu.antoinepurnelle.jobapplication.data.model.TransformationError

fun interface ResumeDtoTransformer {

    /**
     * Transforms a [ResumeDto] into a [Resume].
     *
     * @param input The [ResumeDto] to transform.
     * @return A [RepoResult] containing either the transformed [Resume] on success or a [RepoError] on failure.
     */
    fun transform(input: ResumeDto): RepoResult<Resume, RepoError>

}

class ResumeDtoTransformerImpl : ResumeDtoTransformer {

    @Suppress("ReturnCount") // Allowing multiple return points for clarity in error handling
    override fun transform(
        input: ResumeDto,
    ): RepoResult<Resume, RepoError> {
        // Validate mainInfo and its required fields
        val mainInfoDto = input.record?.mainInfo ?: return getError()
        val name = mainInfoDto.name ?: return getError()
        val headline = mainInfoDto.headline ?: return getError()
        val phoneNumber = mainInfoDto.phoneNumber ?: return getError()
        val email = mainInfoDto.emailAddress ?: return getError()

        val mainInfo = Resume.MainInfo(
            name = name,
            headline = headline,
            pictureUrl = mainInfoDto.pictureUrl,
            location = mainInfoDto.location,
            dateOfBirth = mainInfoDto.dateOfBirth,
            phoneNumber = phoneNumber,
            emailAddress = email,
            linkedIn = mainInfoDto.linkedIn,
            github = mainInfoDto.github,
        )

        return RepoResult.Success(Resume(mainInfo))
    }

    private fun getError() = RepoResult.Error(TransformationError)

}
