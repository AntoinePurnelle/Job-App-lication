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

package eu.antoinepurnelle.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerXSmall
import eu.antoinepurnelle.ui.theme.Dimens.Size.RoundedCornerSizeDefault
import eu.antoinepurnelle.ui.theme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetState = rememberModalBottomSheetState(),
    dragHandle = { DragHandle() },
    shape = RoundedCornerShape(topStart = RoundedCornerSizeDefault, topEnd = RoundedCornerSizeDefault),
    containerColor = colors.decor.popup.background,
    scrimColor = colors.decor.popup.overlay,
) {
    content()
    Spacer(modifier = Modifier.navigationBarsPadding())
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) = Box(
    modifier = modifier
        .padding(vertical = SpacerXSmall)
        .background(color = colors.text.secondary, shape = RoundedCornerShapeDefault)
        .size(width = 52.dp, height = 4.dp),
)
