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

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun launch(type: LaunchType, context: Any?) {
    val urlString = when (type) {
        is LaunchType.Phone -> "tel://${type.number}"
        is LaunchType.Chat -> "sms:${type.number}"
        is LaunchType.Email -> "mailto:${type.address}"
        is LaunchType.Url -> type.url.formatHttps()
    }
    val url = NSURL.URLWithString(urlString)
    if (url != null) {
        UIApplication.sharedApplication.openURL(
            url,
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
    }
}
