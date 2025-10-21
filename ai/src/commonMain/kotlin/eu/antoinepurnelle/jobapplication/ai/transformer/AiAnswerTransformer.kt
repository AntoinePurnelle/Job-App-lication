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

package eu.antoinepurnelle.jobapplication.ai.transformer

import eu.antoinepurnelle.jobapplication.ai.model.AnswerDto
import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.TransformationFailure

fun interface AiAnswerTransformer {
    fun transform(answer: AnswerDto): Result<String, Failure>
}

class AiAnswerTransformerImpl : AiAnswerTransformer {
    override fun transform(answer: AnswerDto): Result<String, Failure> {
        val text = answer.candidates.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text

        return if (text != null) {
            Result.Success(text)
        } else {
            Result.Error(TransformationFailure)
        }
    }
}
