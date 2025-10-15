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

package eu.antoinepurnelle.ui.theme

import androidx.compose.ui.unit.dp

object Dimens {

    object Padding {
        val Screen = 16.dp

        val CardHorizontal = 16.dp
        val CardVertical = 16.dp
        val CardVerticalHalf = CardVertical / 2

        val SpacerDefault = 16.dp
        val SpacerContent = 2.dp
        val SpacerLarge = 24.dp

        val Icon = 12.dp

        val SectionTitleVertical = 16.dp
    }

    object Size {
        val Avatar = 120.dp
        val CompanyAvatar = 48.dp
        val RoundedCornerSizeSmall = 8.dp
        val RoundedCornerSizeDefault = 16.dp
        val ElevationDefault = 4.dp

        val IconDefault = 24.dp

        val DividerThickness = 1.dp
    }

}
