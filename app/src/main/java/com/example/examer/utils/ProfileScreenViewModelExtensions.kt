package com.example.examer.utils

import com.example.examer.viewmodels.ProfileScreenViewModel


fun ProfileScreenViewModel.updateName(newName: String) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.NAME,
        newName,
    )
}

fun ProfileScreenViewModel.updateEmail(newEmail: String) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.EMAIL,
        newEmail
    )
}

fun ProfileScreenViewModel.updatePassword(newPassword: String) {
    updateAttributeForCurrentUser(
        ProfileScreenViewModel.UpdateAttribute.PASSWORD,
        newPassword
    )
}