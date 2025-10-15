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

package eu.antoinepurnelle.jobapplication.util

import java.awt.Desktop
import java.net.URI

actual fun launch(type: LaunchType, context: Any?) {
    val desktop = Desktop.getDesktop()
    when (type) {
        is LaunchType.Phone -> {
            val mailto = URI("tel:${type.number.replace(" ", "")}")
            desktop.browse(mailto)
        }
        is LaunchType.Chat -> {
            val sms = URI("sms:${type.number.replace(" ", "")}")
            desktop.browse(sms)
        }
        is LaunchType.Email -> {
            val mailto = URI("mailto:${type.address}")
            desktop.mail(mailto)
        }
        is LaunchType.Url -> {
            desktop.browse(URI(type.url.formatHttps()))
        }
    }
}
