package de.christian2003.feature.autofill.presentation.ui.auth

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.service.autofill.FillResponse
import android.view.autofill.AutofillId
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.core.security.application.usecases.UnlockWithBiometricsUseCase
import de.christian2003.core.ui.composables.LoadingIndicatorButton
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.core.ui.theme.PasswordVaultTheme
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.feature.autofill.R
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.infrastructure.android.dto.ParcelableAutofillData
import de.christian2003.feature.autofill.presentation.viewmodels.AutofillAuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Activity which allows the user to authenticate before any other app can be auto-filled.
 */
@AndroidEntryPoint
class AutofillAuthActivity : FragmentActivity() {

    companion object {

        /**
         * Extra key with which to pass the autofill data to the activity. The extra must contain
         * a parcelable which can be deserialized into ParcelableAutofillData.
         */
        const val EXTRA_AUTOFILL_DATA: String = "autofill_data"

    }

    /**
     * View model for the activity.
     */
    private val viewModel: AutofillAuthViewModel by viewModels()

    /**
     * Use case to unlock the master key with biometrics. The use case is activity-scoped, which is
     * why it's injected into the activity and not the view model.
     */
    @Inject internal lateinit var unlockWithBiometricsUseCase: UnlockWithBiometricsUseCase


    /**
     * Creates the activity.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //App content:
        enableEdgeToEdge(
            navigationBarStyle = if (isNightMode()) {
                SystemBarStyle.dark(
                    scrim = Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT
                )
            }
        )
        setContent {
            AutofillActivityContent(
                viewModel = viewModel,
                onBiometricUnlock = {
                    try {
                        unlockWithBiometricsUseCase.unlock()
                    } catch (_: Exception) {
                        false
                    }
                },
                onDismiss = {
                    setResult(RESULT_CANCELED)
                    finish()
                },
                onConfirm = {
                    onFinishAutofill()
                }
            )
        }
    }


    /**
     * Invoked to finish autofill after successfully unlocking the master key. Prerequisite for this
     * method is that the master key is unlocked.
     */
    private suspend fun onFinishAutofill() {
        val myIntent: Intent = intent
        val replyIntent = Intent()

        val autofillData: ParcelableAutofillData? = myIntent.extras?.getParcelable(EXTRA_AUTOFILL_DATA, ParcelableAutofillData::class.java)
        if (autofillData != null) {
            val autofillTypes: Map<AutofillType, List<AutofillId>> = autofillData.fieldMap
            val capabilities: List<AccountCapability> = autofillData.capabilities
            val fillResponse: FillResponse = viewModel.fetchAutofillData(
                packageName = this.packageName,
                autofillTypes = autofillTypes,
                focusedAutofillPartition = autofillData.focusedAutofillPartition,
                capabilities = capabilities
            )

            replyIntent.putExtra(android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT, fillResponse)

            setResult(RESULT_OK, replyIntent)
            finish()
            return
        }
        setResult(RESULT_CANCELED)
        finish()
    }


    /**
     * Determines whether the system is in night or day mode.
     *
     * @return  Whether the system is night or day mode.
     */
    private fun isNightMode(): Boolean {
        val currentMode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }

}


/**
 * Composable content for the autofill activity. This shows a bottom sheet dialog through which
 * the user can authenticate.
 *
 * @param viewModel         View model for the activity.
 * @param onBiometricUnlock Callback invoked to perform biometric unlock of the master key.
 * @param onDismiss         Callback invoked to dismiss of the dialog.
 * @param onConfirm         Callback invoked to dismiss the dialog after successfully unlocking the
 *                          master key.
 */
@Composable
private fun AutofillActivityContent(
    viewModel: AutofillAuthViewModel,
    onBiometricUnlock: suspend () -> Boolean,
    onDismiss: () -> Unit,
    onConfirm: suspend () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val sheetState: SheetState = rememberModalBottomSheetState()
    val focusManager: FocusManager = LocalFocusManager.current

    val invokeOnConfirm: () -> Unit = {
        coroutineScope.launch(Dispatchers.Default) {
            viewModel.unlockMasterKey()
            if (!viewModel.isUnlockingMasterKey && viewModel.isPasswordValid) {
                withContext(Dispatchers.Main) {
                    focusManager.clearFocus()
                    onConfirm()
                }
            }
        }
    }

    PasswordVaultTheme(
        dynamicColor = viewModel.useGlobalTheme,
        contrast = viewModel.themeContrast
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                    )
            ) {
                DragHandle()
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.launcher_foreground_fullscale),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                )
                Text(
                    text = stringResource(R.string.autofill_auth_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                TextInput(
                    value = viewModel.password,
                    onValueChange = {
                        viewModel.password = it
                    },
                    label = stringResource(R.string.autofill_auth_labelMasterPassword),
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            focusManager.clearFocus()
                            invokeOnConfirm()
                        }
                    ),
                    errorMessage = when {
                        !viewModel.isPasswordValid && viewModel.password.isBlank() -> stringResource(de.christian2003.core.ui.R.string.error_blankInput)
                        !viewModel.isPasswordValid -> stringResource(de.christian2003.core.ui.R.string.error_invalidPassword)
                        else -> null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )
                if (viewModel.areBiometricsConfigured) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                if (onBiometricUnlock()) {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        onConfirm()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_biometrics),
                                contentDescription = "",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(stringResource(de.christian2003.core.ui.R.string.button_biometrics))
                        }
                    }
                }
                LoadingIndicatorButton(
                    label = stringResource(de.christian2003.core.ui.R.string.button_authenticate),
                    isLoading = viewModel.isUnlockingMasterKey,
                    onClick = {
                        focusManager.clearFocus()
                        invokeOnConfirm()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


/**
 * Custom drag handle for the bottom sheet. We use this custom drag handle, because I do not like
 * the visual appearance of the default drag handle.
 *
 * @param modifier  Modifier.
 */
@Composable
private fun DragHandle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .size(
                width = 48.dp,
                height = 4.dp
            )
            .clip(
                RoundedCornerShape(2.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    )
}
