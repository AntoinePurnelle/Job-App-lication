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

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.datetime.LocalDate

actual fun formatLocalDate(date: LocalDate): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.month.ordinal)
        set(Calendar.DAY_OF_MONTH, date.day)
    }
    return sdf.format(cal.time)
}
