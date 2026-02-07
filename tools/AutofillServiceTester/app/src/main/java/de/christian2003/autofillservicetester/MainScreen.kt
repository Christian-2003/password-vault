package de.christian2003.autofillservicetester

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun MainScreen(
    onContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Login")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp
                )
        ) {
            GroupHeader("Login")
            AutofillInput(
                type = ContentType.Username,
                label = "Username"
            )
            AutofillInput(
                type = ContentType.Password,
                label = "Password"
            )
            AutofillInput(
                type = ContentType.NewUsername,
                label = "NewUsername"
            )
            AutofillInput(
                type = ContentType.NewPassword,
                label = "NewPassword"
            )
            AutofillInput(
                type = ContentType.SmsOtpCode,
                label = "SmsOtpCode"
            )


            GroupHeader("Address")
            AutofillInput(
                type = ContentType.PostalCode,
                label = "PostalCode"
            )
            AutofillInput(
                type = ContentType.AddressAuxiliaryDetails,
                label = "AddressAuxiliaryDetails"
            )
            AutofillInput(
                type = ContentType.AddressCountry,
                label = "AddressCountry"
            )
            AutofillInput(
                type = ContentType.AddressLocality,
                label = "AddressLocality"
            )
            AutofillInput(
                type = ContentType.AddressRegion,
                label = "AddressRegion"
            )
            AutofillInput(
                type = ContentType.AddressStreet,
                label = "AddressStreet"
            )
            AutofillInput(
                type = ContentType.PostalAddress,
                label = "PostalAddress"
            )
            AutofillInput(
                type = ContentType.PostalCodeExtended,
                label = "PostalCodeExtended"
            )


            GroupHeader("Birthday")
            AutofillInput(
                type = ContentType.BirthDateDay,
                label = "BirthDateDay"
            )
            AutofillInput(
                type = ContentType.BirthDateFull,
                label = "BirthDateFull"
            )
            AutofillInput(
                type = ContentType.BirthDateMonth,
                label = "BirthDateMonth"
            )
            AutofillInput(
                type = ContentType.BirthDateYear,
                label = "BirthDateYear"
            )


            GroupHeader("Credit Card")
            AutofillInput(
                type = ContentType.CreditCardExpirationDate,
                label = "CreditCardExpirationDate"
            )
            AutofillInput(
                type = ContentType.CreditCardExpirationDay,
                label = "CreditCardExpirationDay"
            )
            AutofillInput(
                type = ContentType.CreditCardExpirationMonth,
                label = "CreditCardExpirationMonth"
            )
            AutofillInput(
                type = ContentType.CreditCardExpirationYear,
                label = "CreditCardExpirationYear"
            )
            AutofillInput(
                type = ContentType.CreditCardNumber,
                label = "CreditCardNumber"
            )
            AutofillInput(
                type = ContentType.CreditCardSecurityCode,
                label = "CreditCardSecurityCode"
            )


            GroupHeader("Personal Info")
            AutofillInput(
                type = ContentType.Gender,
                label = "Gender"
            )
            AutofillInput(
                type = ContentType.EmailAddress,
                label = "EmailAddress"
            )
            AutofillInput(
                type = ContentType.PersonFirstName,
                label = "PersonFirstName"
            )
            AutofillInput(
                type = ContentType.PersonFullName,
                label = "PersonFullName"
            )
            AutofillInput(
                type = ContentType.PersonLastName,
                label = "PersonLastName"
            )
            AutofillInput(
                type = ContentType.PersonMiddleInitial,
                label = "PersonMiddleInitial"
            )
            AutofillInput(
                type = ContentType.PersonMiddleName,
                label = "PersonMiddleName"
            )
            AutofillInput(
                type = ContentType.PersonNamePrefix,
                label = "PersonNamePrefix"
            )
            AutofillInput(
                type = ContentType.PersonNameSuffix,
                label = "PersonNameSuffix"
            )


            GroupHeader("Phone Number")
            AutofillInput(
                type = ContentType.PhoneCountryCode,
                label = "PhoneCountryCode"
            )
            AutofillInput(
                type = ContentType.PhoneNumber,
                label = "PhoneNumber"
            )
            AutofillInput(
                type = ContentType.PhoneNumberDevice,
                label = "PhoneNumberDevice"
            )
            AutofillInput(
                type = ContentType.PhoneNumberNational,
                label = "PhoneNumberNational"
            )
        }
    }
}


@Composable
private fun GroupHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 24.dp)
    )
}


@Composable
private fun AutofillInput(
    type: ContentType,
    label: String
) {
    var value: String by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
        },
        label = {
            Text(label)
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentType = type
            }
    )
}
