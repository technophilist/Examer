package com.example.examer.viewmodels.profileScreenViewModel


fun ProfileScreenViewModel.updateName(
    newName: String,
    resetStateTimeOut: Long = defaultResetStateTimeOut
) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.NAME,
        newName,
        resetStateTimeOut
    )
}

fun ProfileScreenViewModel.updateEmail(
    newEmail: String,
    resetStateTimeOut: Long = defaultResetStateTimeOut
) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.EMAIL,
        newEmail,
        resetStateTimeOut
    )
}

fun ProfileScreenViewModel.updatePassword(
    newPassword: String,
    resetStateTimeOut: Long = defaultResetStateTimeOut
) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.PASSWORD,
        newPassword,
        resetStateTimeOut
    )
}