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

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual fun launch(type: LaunchType, context: Any?) {
    if (context is Context) {
        when (type) {
            is LaunchType.Phone -> {
                val intent = Intent(Intent.ACTION_DIAL, "tel:${type.number}".toUri())
                context.startActivity(intent)
            }
            is LaunchType.Chat -> {
                val intent = Intent(Intent.ACTION_VIEW, "sms:${type.number}".toUri())
                context.startActivity(intent)
            }
            is LaunchType.Email -> {
                val intent = Intent(Intent.ACTION_SENDTO, "mailto:${type.address}".toUri())
                context.startActivity(intent)
            }
            is LaunchType.Url -> {
                val intent = Intent(Intent.ACTION_VIEW, type.url.formatHttps().toUri())
                context.startActivity(intent)
            }
        }
    }
}
