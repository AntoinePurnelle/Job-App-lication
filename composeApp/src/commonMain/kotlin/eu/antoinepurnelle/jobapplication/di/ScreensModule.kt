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

package eu.antoinepurnelle.jobapplication.di

import eu.antoinepurnelle.jobapplication.domain.transformer.ExperienceUiTransformer
import eu.antoinepurnelle.jobapplication.domain.transformer.MainUiTransformer
import eu.antoinepurnelle.jobapplication.experience.ExperienceViewModel
import eu.antoinepurnelle.jobapplication.experience.transformer.ExperienceUiTransformerImpl
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel
import eu.antoinepurnelle.jobapplication.mainscreen.transformer.MainUiTransformerImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val screensModule = module {
    viewModelOf(::MainScreenViewModel)
    factoryOf(::MainUiTransformerImpl) { bind<MainUiTransformer>() }
    viewModelOf(::ExperienceViewModel)
    factoryOf(::ExperienceUiTransformerImpl) { bind<ExperienceUiTransformer>() }
}
