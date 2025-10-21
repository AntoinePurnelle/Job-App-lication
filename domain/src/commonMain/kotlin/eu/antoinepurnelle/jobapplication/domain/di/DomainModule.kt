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

package eu.antoinepurnelle.jobapplication.domain.di

import eu.antoinepurnelle.jobapplication.domain.usecase.FetchMainPageUseCase
import eu.antoinepurnelle.jobapplication.domain.usecase.FetchMainPageUseCaseImpl
import eu.antoinepurnelle.jobapplication.domain.usecase.GetExperiencePageUseCase
import eu.antoinepurnelle.jobapplication.domain.usecase.GetExperiencePageUseCaseImpl
import eu.antoinepurnelle.jobapplication.domain.usecase.PromptUseCase
import eu.antoinepurnelle.jobapplication.domain.usecase.PromptUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::FetchMainPageUseCaseImpl) { bind<FetchMainPageUseCase>() }
    factoryOf(::GetExperiencePageUseCaseImpl) { bind<GetExperiencePageUseCase>() }
    factoryOf(::PromptUseCaseImpl) { bind<PromptUseCase>() }
}
