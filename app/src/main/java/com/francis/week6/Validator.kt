package com.francis.week6

import android.view.View
import com.francis.week6.models.Contact
import com.google.android.material.textfield.TextInputEditText

class Validator {
    companion object {
        fun editTextValidate(view1: View, view2: View, view3: View): Boolean {
            return ((view1 as TextInputEditText).text?.isBlank() == true &&
                    (view2 as TextInputEditText).text?.isBlank() == true &&
                    (view3 as TextInputEditText).text?.isBlank() == true)
        }

        fun setContactName(contact: Contact): Contact{
            contact.fullName = if (contact.fullName == "") {
                if (contact.email != "") {
                    contact.email
                } else {
                    contact.phone
                }
            } else contact.fullName

            return contact
        }
    }
}