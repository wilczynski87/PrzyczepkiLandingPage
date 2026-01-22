package com.example.przyczepki_landingpage.ui.modal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.przyczepki_landingpage.AppState
import com.example.przyczepki_landingpage.AppViewModel
import com.example.przyczepki_landingpage.data.ModalType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModals(
    viewModel: AppViewModel
) {
    val state by viewModel.appState.collectAsState()
    val modal = state.modal

    when (modal) {
        ModalType.CONFIRM_RESERVATION -> {
            ModalBottomSheet(
                onDismissRequest = viewModel::closeModal,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                ConfirmReservationSheet(
                    viewModel,
                    onCancel = { viewModel.closeModal() },
                    onConfirm = {
                        viewModel.reserve()
                        viewModel.closeModal()
                    },
                )
            }
        }

        ModalType.TRAILER_DETAILS -> {
            ModalBottomSheet(
                onDismissRequest = viewModel::closeModal
            ) {
//                TrailerDetailsSheet(
//                    trailer = state.selectedTrailer,
//                    onClose = viewModel::closeModal
//                )
            }
        }

        ModalType.NONE -> Unit
        else -> {}
    }
}
