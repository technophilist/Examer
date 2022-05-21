package com.example.examer.viewmodels.profileScreenViewModel

/**
 * A utility method that is used to update the name of the currently
 * logged-in user with the [newName]. This extension method is a
 * shorthand for the [ProfileScreenViewModel.updateAttributeForCurrentUser].
 * It removes the need for specifying the 'updateAttribute' param of the
 * [ProfileScreenViewModel.updateAttributeForCurrentUser] method.
 */
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

/**
 * A utility method that is used to update the email of the currently
 * logged-in user with the [newEmail]. This extension method is a
 * shorthand for the [ProfileScreenViewModel.updateAttributeForCurrentUser].
 * It removes the need for specifying the 'updateAttribute' param of the
 * [ProfileScreenViewModel.updateAttributeForCurrentUser] method.
 */
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

/**
 * A utility method that is used to update the password of the currently
 * logged-in user with the [newPassword]. This extension method is a
 * shorthand for the [ProfileScreenViewModel.updateAttributeForCurrentUser].
 * It removes the need for specifying the 'updateAttribute' param of the
 * [ProfileScreenViewModel.updateAttributeForCurrentUser] method.
 */
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